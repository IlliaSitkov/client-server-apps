package processing;

import exceptions.CipherException;
import packet.Packet;
import packet.PacketEncryptor;

public class Decryptor extends BaseMultiThreadUnit {

	private static Decryptor instance;
	
	private Mediator mediator;
	
	public static Decryptor getInstance() {
		if(instance == null)
			instance = new Decryptor();
		return instance;
	}
	
	private Decryptor() {
		super();
		this.mediator = Mediator.getInstance();
	}
	
	public void addDecryptionTask(byte[] encryptedMessage) {
		this.execService.execute(() -> {
			try {
				var result = PacketEncryptor.decryptPacket(encryptedMessage);
				for(Packet p : result)
					this.mediator.notifyPacketDecrypted(p);
			} catch (CipherException e) {
				e.printStackTrace();
			}
		});
	}
	
	
	
}
