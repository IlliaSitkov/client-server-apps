package processing;

import java.io.IOException;
import java.util.Optional;

import packet.Packet;
import server.StoreServer;
import server.udp.StoreServerUDP;

public class Mediator {
	
	private static Mediator instance;
	
	private final Decryptor decryptor;
	
	private final Encryptor encryptor;
	
	private final Processor processor;
	
	private final Sender sender;

	private final StoreServer server;
	
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
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void startServer() {
		server.receive();
	}
	
	public void receiveMessage(byte[] messageBytes) {
		this.decryptor.addDecryptionTask(messageBytes);
	}
	
	public void notifyPacketDecrypted(Packet packet) {
		this.processor.addProcessingTask(packet);
	}
		
	public void notifyPacketProcessed(Packet initial, boolean success, Optional<Object> result, Optional<String> errorMessage) {
		this.encryptor.addEncryptionTask(initial, success, result, errorMessage);
	}
	
	public void notifyPacketEncrypted(byte[] encryptedPacket) {
		this.sender.sendPacket(encryptedPacket);
	}

	public void notifyPacketSentToServer(byte[] bytes) {
		server.send(bytes);
	}
	
	public void terminateAll() throws InterruptedException {
		server.stop();
		this.decryptor.terminate();
		this.processor.terminate();
		this.encryptor.terminate();	
	}
		
}
