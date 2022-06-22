package processing;

import java.util.Optional;

import packet.Packet;

public class Mediator {
	
	private static Mediator instance;
	
	private Decryptor decryptor;
	
	private Encryptor encryptor;
	
	private Processor processor;
	
	private Sender sender;
	
	public static Mediator getInstance() {
		if(instance == null)
			instance = new Mediator();
		return instance;
	}
	
	private Mediator() {
		this.decryptor = Decryptor.getInstance(this);
		this.encryptor = Encryptor.getInstance(this);
		this.processor = Processor.getInstance(this);
		this.sender = Sender.getInstance();
	}
	
	
	public void receiveMessage(byte[] messageBytes) {
		this.decryptor.addDecryptionTask(messageBytes);
	}
	
	public void notifyPacketDecrypted(Packet packet) {
		this.processor.addProcessingTask(packet);
	}
	
	public void notifyPacketProcessed(Packet initial, Optional<Integer> processingResult) {
		this.encryptor.addEncryptionTask(initial, processingResult);
	}
	
	public void notifyPacketEncrypted(byte[] encryptedPacket) {
		this.sender.sendPacket(encryptedPacket);
	}
	
	public void terminateAll() {
		this.decryptor.terminate();
		this.processor.terminate();
		this.encryptor.terminate();	
	}
		
}
