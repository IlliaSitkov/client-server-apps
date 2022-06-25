package server.udp;

import exceptions.CipherException;
import exceptions.PacketDecryptionException;
import exceptions.PacketNoResponseException;
import packet.Packet;
import packet.PacketEncryptor;
import server.StoreClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

public class StoreClientUDP implements StoreClient {

    private final DatagramSocket datagramSocket;

    private static final int NUMBER_OF_RETRIES = 5;

    private static final int RECEIVE_TIMEOUT = 3000;


    public StoreClientUDP(int port) throws IOException {
        this.datagramSocket = new DatagramSocket(port, StoreServerUDP.DEFAULT_SERVER_ADDRESS);
        datagramSocket.setSoTimeout(RECEIVE_TIMEOUT);
    }

    public StoreClientUDP() throws IOException {
        this.datagramSocket = new DatagramSocket();
        datagramSocket.setSoTimeout(RECEIVE_TIMEOUT);
    }

    public void send(byte[] bytes) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, StoreServerUDP.DEFAULT_SERVER_ADDRESS, StoreServerUDP.DEFAULT_PORT);
        datagramSocket.send(datagramPacket);
        System.out.println(this + " Client sent bytes: "+Arrays.toString(bytes));
    }

    public Packet receive() throws IOException {
        byte[] bytes = new byte[Packet.PACKET_MAX_LENGTH];
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
        datagramSocket.receive(datagramPacket);
        return getPacketFromBytes(datagramPacket.getData());
    }


    public Packet fetch(Packet packet) {
        byte[] encryptedPacket;
        try {
             encryptedPacket = PacketEncryptor.encryptPacket(packet);
        } catch (CipherException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < NUMBER_OF_RETRIES; i++) {
            try {
                send(encryptedPacket);
                Packet packetReceived = receive();
                if (packetReceived.getBPktId() == packet.getBPktId()) {
                    System.out.println(this+" RETURNED "+packetReceived);
                    return packetReceived;
                } else {
                    System.out.println(this+" EXPECTED = "+packet.getBPktId() + ", RECEIVED = "+packetReceived.getBPktId());
                }
            } catch (SocketTimeoutException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        throw new PacketNoResponseException();
    }


    private Packet getPacketFromBytes(byte[] bytes) {
        try {
            System.out.println(this+" Client received bytes: "+Arrays.toString(bytes));
            List<Packet> packets = PacketEncryptor.decryptPacket(bytes);
            if (packets.size() > 0) {
                Packet packet = packets.get(0);
                System.out.println("Client received: "+packet);
                return packet;
            }
            System.out.println(this+" Client could not decrypt bytes: "+Arrays.toString(bytes));
            throw new PacketDecryptionException();
        } catch (CipherException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        datagramSocket.close();
    }


    @Override
    public String toString() {
        return datagramSocket.getLocalAddress().toString()+":"+datagramSocket.getPort();
    }
}
