package processing;

import java.io.OutputStream;
import java.util.Optional;

import exceptions.CipherException;
import packet.Packet;
import packet.PacketEncryptor;
import utils.Commands;

public class Encryptor extends BaseMultiThreadUnit {

	private static Encryptor instance;
	
	private Mediator mediator;
	
	public static Encryptor getInstance(Mediator mediator) {
		if(instance == null)
			instance = new Encryptor(mediator);
		return instance;
	}
	
	private Encryptor(Mediator mediator) {
		super();
		this.mediator = mediator;
	}
	
	//public void addEncryptionTask(Packet initial, Optional<Integer> processingResult) {
	public void addEncryptionTask(Packet initial, boolean success, Optional<Object> result, Optional<String> errorMessage, OutputStream outStream) {
		boolean getQuantityQuery = initial.getBMsg().getCType() == Commands.PRODUCT_GET_QUANTITY.ordinal();
		this.execService.execute(() -> {
			//temporary solution
			Packet packet = new Packet(initial.getBSrc(), 
							initial.getBPktId(),
							initial.getBMsg().getCType(),
							initial.getBMsg().getUserId(),
							"{\"result\": " + (getQuantityQuery ? ((Integer)result.get()) : "\"OK\"") + "}");
			try {
				byte[] bytes = PacketEncryptor.encryptPacket(packet);
				if(outStream == null)
					this.mediator.notifyPacketEncrypted(bytes);
				else 
					this.mediator.notifyPacketEncrypted(bytes, outStream);
			} catch (CipherException e) {
				e.printStackTrace();
			}
			
		});
	}
	
}
