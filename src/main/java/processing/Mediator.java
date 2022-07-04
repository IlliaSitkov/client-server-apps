package processing;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import packet.Packet;
import server.StoreServer;
import server.tcp.StoreServerTCP;
import server.udp.StoreServerUDP;

public class Mediator {
	
	private static Mediator instance;
	
	private final Decryptor decryptor;
	
	private final Encryptor encryptor;
	
	private final Processor processor;
	
	private final Sender sender;

	private final StoreServer server;
	
	//?? try to create unified interface
	private final StoreServerTCP tcpServer;
	
	public static Mediator getInstance() {
		if(instance == null)
			instance = new Mediator();
		return instance;
	}
	
	private Mediator() {
		this.decryptor = Decryptor.getInstance(this);
		this.encryptor = Encryptor.getInstance(this);
		this.processor = Processor.getInstance(this);
		this.sender = Sender.getInstance(this);
		try {
			this.server = new StoreServerUDP(this);
			this.tcpServer = new StoreServerTCP(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void startServer() {
		server.receive();
	}
	
	public void startTCPServer() {
		this.tcpServer.start();
	}
	
	public void stopTCPServer() throws IOException, InterruptedException {
		this.tcpServer.stop();
	}
	
	public void receiveMessage(byte[] messageBytes) {
		this.decryptor.addDecryptionTask(messageBytes);
	}
	
	public void receiveMessage(byte[] messageBytes, OutputStream outStream) {
		this.decryptor.addDecryptionTask(messageBytes, outStream);
	}
	
	public void notifyPacketDecrypted(Packet packet) {
		this.processor.addProcessingTask(packet, null);
	}
	
	public void notifyPacketDecrypted(Packet packet, OutputStream outStream) {
		this.processor.addProcessingTask(packet, outStream);
	}
		
	public void notifyPacketProcessed(Packet initial, boolean success, Optional<Object> result, Optional<String> errorMessage) {
		this.encryptor.addEncryptionTask(initial, success, result, errorMessage, null);
	}
	
	public void notifyPacketProcessed(Packet initial, boolean success, Optional<Object> result, Optional<String> errorMessage, OutputStream outStream) {
		this.encryptor.addEncryptionTask(initial, success, result, errorMessage, outStream);
	}
	
	public void notifyPacketEncrypted(byte[] encryptedPacket) {
		this.sender.sendPacket(encryptedPacket);
	}
	
	public void notifyPacketEncrypted(byte[] encryptedPacket, OutputStream outStream) {
		this.sender.sendPacket(encryptedPacket, outStream);
	}

	public void notifyPacketSentToServer(byte[] encryptedPacket, OutputStream outStream) {
		try {
			outStream.write(encryptedPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void notifyPacketSentToServer(byte[] bytes) {
		server.send(bytes);
	}
	
	public void terminateAll() throws InterruptedException, IOException {
		this.server.stop();
		this.tcpServer.stop();
		this.decryptor.terminate();
		this.processor.terminate();
		this.encryptor.terminate();	
	}
		
}
