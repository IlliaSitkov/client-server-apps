package server.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import exceptions.CipherException;
import exceptions.PacketDecryptionException;
import exceptions.ServerIsDownException;
import packet.Packet;
import packet.PacketEncryptor;
import utils.Utils;

public class StoreClientTCP {
	
	private static final int RECCONECT_ATTEMPS = 3;
	
	private static final int RECEIVE_TIMEOUT = 3000;
	
	private int socketPort;
	private Socket socket;
	
	public StoreClientTCP(InetAddress address, int port) throws IOException {
		this.socketPort = port;
		this.socket = new Socket(address, this.socketPort);
		this.socket.setSoTimeout(RECEIVE_TIMEOUT);
	}
	
	public StoreClientTCP() throws IOException {
		this(StoreServerTCP.DEFAULT_SERVER_ADDRESS, StoreServerTCP.DEFAULT_SERVER_TCP_PORT);
	}
	
	public void close() throws IOException {
		this.socket.getOutputStream().write(101);
		this.socket.close();
	}
	
	public boolean serverResponsesOnHeartbeat() throws IOException {
		try {
			this.socket.getOutputStream().write(100);
			int answ = this.socket.getInputStream().read();
			return answ == 100;
		} catch(IOException e) {
			System.out.println("Server not responding");
			return false;
		}		
	}
	
	private void tryToReconnectIfNeeded() throws IOException {
		int cnt = 0;
		while(!serverResponsesOnHeartbeat()) {
			try {
				if(++cnt >= RECCONECT_ATTEMPS)
					throw new ServerIsDownException();
				System.out.println("Trying to reconnect to server: " + cnt);
				Utils.sleep(500);
				this.socket = new Socket(StoreServerTCP.DEFAULT_SERVER_ADDRESS, this.socketPort);
				this.socket.setSoTimeout(RECEIVE_TIMEOUT);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	
	public void send(byte[] bytes) throws IOException {
		tryToReconnectIfNeeded();
		this.socket.getOutputStream().write(bytes);
		System.out.println("CLIENT: Byte array has been sent to " + StoreServerTCP.DEFAULT_SERVER_ADDRESS);
	}

	public Packet receive() throws IOException {
		byte[] bytes = new byte[Packet.PACKET_MAX_LENGTH];
		this.socket.getInputStream().read(bytes);
		try {
			List<Packet> list = PacketEncryptor.decryptPacket(bytes);
			if(list.size() > 0) {
				Packet packet = list.get(0);
				System.out.println("CLIENT: Received packet from server: " + packet.toString());
				return packet;
			}
			throw new PacketDecryptionException();
		} catch (CipherException e) {
			throw new RuntimeException(e);
		}
	}

	public Packet fetch(Packet packet) {
		try {
			tryToReconnectIfNeeded();
			byte[] encryptedPacket = PacketEncryptor.encryptPacket(packet);
			this.socket.getOutputStream().write(encryptedPacket);
			return this.receive();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
	}

}
