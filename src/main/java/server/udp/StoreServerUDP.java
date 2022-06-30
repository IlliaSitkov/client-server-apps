package server.udp;

import exceptions.CipherException;
import exceptions.PacketDecryptionException;
import exceptions.PacketDuplicatedException;
import exceptions.ServerAlreadyStoppedException;
import message.Message;
import packet.Packet;
import packet.PacketEncryptor;
import processing.Mediator;
import server.StoreServer;
import utils.Utils;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StoreServerUDP implements StoreServer {

    public static final int DEFAULT_PORT = 8081;

    private final Set<Long> receivedPackets = new HashSet<>();
    public static final InetAddress DEFAULT_SERVER_ADDRESS;

    public static final int RECEIVE_TIMEOUT = 3000;

    private final Lock lock = new ReentrantLock(true);

    private final ConcurrentHashMap<Long, DatagramPacket> addressMap = new ConcurrentHashMap<>();

    static {
        try {
            DEFAULT_SERVER_ADDRESS = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final DatagramSocket datagramSocket;

    private volatile boolean isRunning = false;

    private final Mediator mediator;


    public StoreServerUDP(Mediator mediator, InetAddress address, int port) throws IOException {
        this.datagramSocket = new DatagramSocket(port, address);
        datagramSocket.setSoTimeout(RECEIVE_TIMEOUT);
        this.mediator = mediator;
    }

    public StoreServerUDP(Mediator mediator) throws IOException {
        this(mediator, DEFAULT_SERVER_ADDRESS, DEFAULT_PORT);
    }

    public void stop() throws InterruptedException {
        try {
            lock.lock();
            isRunning = false;
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.DAYS);
            datagramSocket.close();
            System.out.println("Server stopped");
        } finally {
            lock.unlock();
        }
    }


    public void receive() {
        if (datagramSocket.isClosed()) {
            throw new ServerAlreadyStoppedException();
        }
        isRunning = true;
        System.out.println("Server is running...");
        new Thread(() -> {
            while (isRunning) {
                try {
                    lock.lock();
                    if (isRunning) {
                        byte[] bytes = new byte[Packet.PACKET_MAX_LENGTH];
                        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
                        try {
                            datagramSocket.receive(datagramPacket);
                        } catch (SocketTimeoutException e) {
                            System.out.println("Server is waiting for a client...");
                            continue;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        executorService.execute(getDatagramRunnable(datagramPacket));
                    } else {
                        break;
                    }
                } finally {
                    lock.unlock();
                }
            }
        }).start();
    }

    private Runnable getDatagramRunnable(DatagramPacket datagramPacket) {
        return () -> {
            byte[] data = datagramPacket.getData();
            long packetId = Utils.getPacketId(data);
            synchronized (receivedPackets) {
                if (receivedPackets.contains(packetId)) {
                    System.out.println("Server rejected: " + Arrays.toString(data));
                    throw new PacketDuplicatedException(packetId);
                }
                receivedPackets.add(packetId);
            }
            System.out.println("Server received: "+Arrays.toString(data));

            addressMap.put(packetId, datagramPacket);
            mediator.receiveMessage(data);
        };
    }


    private Packet receive(byte[] bytes) {
        try {
            System.out.println("Server received byte array: "+Arrays.toString(bytes));
            List<Packet> packets = PacketEncryptor.decryptPacket(bytes);
            if (packets.size() < 1) {
                throw new PacketDecryptionException();
            }
            Packet packet = packets.get(0);
            synchronized (receivedPackets) {
                if (receivedPackets.contains(packet.getBPktId())) {
                    System.out.println("Server rejected: " + packet);
                    throw new PacketDuplicatedException(packet.getBPktId());
                }
                receivedPackets.add(packet.getBPktId());
            }
            System.out.println("Server received: "+packet);
            return packet;
        } catch (CipherException e) {
            throw new RuntimeException(e);
        }
    }


    public synchronized void send(byte[] bytes, InetAddress inetAddress, int port) {
        try {
            System.out.println("Server sent bytes: "+Arrays.toString(bytes));
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, inetAddress, port);
            System.out.println("Server dg to "+datagramPacket.getAddress() +":"+datagramPacket.getPort());
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void send(byte[] bytes) {
        long packetId = Utils.getPacketId(bytes);
        DatagramPacket datagramPacket = addressMap.get(packetId);
        send(bytes, datagramPacket.getAddress(), datagramPacket.getPort());
        addressMap.remove(packetId, datagramPacket);

    }

    //https://wickesit.atlassian.net/jira/software/c/projects/MSV/boards/192?selectedIssue=MSV-2468&quickFilter=428
    //Roman Yakymchuk11:44
    //https://wickesit.atlassian.net/

}
