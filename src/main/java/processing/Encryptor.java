package processing;

import java.util.Optional;

import exceptions.CipherException;
import packet.Packet;
import packet.PacketEncryptor;

public class Encryptor extends BaseMultiThreadUnit {

	private static Encryptor instance;
	
	private Mediator mediator;
	
	public static Encryptor getInstance() {
		if(instance == null)
			instance = new Encryptor();
		return instance;
	}
	
	private Encryptor() {
		super();
		this.mediator = Mediator.getInstance();
	}
	
	public void addEncryptionTask(Packet initial, Optional<Integer> processingResult) {
		this.execService.execute(() -> {
			//temporary solution
			Packet packet = new Packet(initial.getBSrc(), 
							initial.getBPktId(), 
							initial.getBMsg().getCType(),
							initial.getBMsg().getUserId(),
							"OK");
			try {
				byte[] bytes = PacketEncryptor.encryptPacket(packet);
				this.mediator.notifyPacketEncrypted(bytes);
			} catch (CipherException e) {
				e.printStackTrace();
			}
			
		});
	}
	
}
