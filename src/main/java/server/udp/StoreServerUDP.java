package server.udp;

import exceptions.CipherException;
import exceptions.PacketDecryptionException;
import exceptions.PacketDuplicatedException;
import exceptions.ServerAlreadyStoppedException;
import message.Message;
import packet.Packet;
import packet.PacketEncryptor;
import server.StoreServer;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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


    public StoreServerUDP(InetAddress address, int port) throws IOException {
        this.datagramSocket = new DatagramSocket(port, address);
        datagramSocket.setSoTimeout(RECEIVE_TIMEOUT);
    }

    public StoreServerUDP() throws IOException {
        this(DEFAULT_SERVER_ADDRESS, DEFAULT_PORT);
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
            System.out.println("SERVER RECEIVED DG: "+datagramPacket+" | "+datagramPacket.getPort()+", "+Arrays.toString(datagramPacket.getData()));
            try {
                Packet packet = receive(datagramPacket.getData());// use mediator and its decryptor instead

                // somehow process the packet and get the response packet
                // process(packet)
                // response = ...

                Packet res = new Packet(packet.getBSrc(), packet.getBPktId(), new Message(0, 0, "{\"result\":\"OK\"}"));
                send(PacketEncryptor.encryptPacket(res), datagramPacket.getAddress(), datagramPacket.getPort());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
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



}
