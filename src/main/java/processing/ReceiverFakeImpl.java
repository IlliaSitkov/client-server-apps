package processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import exceptions.CipherException;
import packet.Packet;
import packet.PacketEncryptor;
import utils.JSONStrings;

public class ReceiverFakeImpl implements Receiver {

	private static Receiver instance;
	
	private Mediator mediator;
	
	private List<Packet> hardcodedPackets;
	
	public static Receiver getInstance() {
		if(instance == null)
			instance = new ReceiverFakeImpl();
		return instance;
	}
	
	private ReceiverFakeImpl() {
		this.mediator = Mediator.getInstance();
		this.hardcodedPackets = new ArrayList<>();
		this.hardcodedPackets.add(new Packet((byte)2, 3245L, 0, 1, "{\""+ JSONStrings.PRODUCT_ID +"\":1}"));
		this.hardcodedPackets.add(new Packet((byte)2, 1234L, 1, 2, "{\""+ JSONStrings.PRODUCT_ID +"\":1, \""
				+ JSONStrings.QUANTITY_TO_REMOVE + "\": 5}"));
		this.hardcodedPackets.add(new Packet((byte)2, 1235L, 2, 2, "{\""+ JSONStrings.PRODUCT_ID +"\":1, \""
				+ JSONStrings.QUANTITY_TO_ADD + "\": 15}"));
		this.hardcodedPackets.add(new Packet((byte)2, 1000L, 4, 1, "{\""+ JSONStrings.PRODUCT_ID +"\":1, \""
				+ JSONStrings.PRICE + "\": 200.0}"));
	}
	
	@Override
	public void receiveMessage() {
		Random rand = new Random();
		Packet packet = this.hardcodedPackets.get(rand.nextInt(this.hardcodedPackets.size()));
		try {
			byte[] arr  = PacketEncryptor.encryptPacket(packet);
			this.mediator.receiveMessage(arr);
		} catch (CipherException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void receiveMessage(byte[] bytes) {
		this.mediator.receiveMessage(bytes);
	}

}
