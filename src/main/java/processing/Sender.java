package processing;

import java.util.Base64;

public class Sender {

	private static Sender instance;

	private final Mediator mediator;

	public static Sender getInstance(Mediator mediator) {
		if(instance == null)
			instance = new Sender(mediator);
		return instance;
	}

	private Sender(Mediator mediator) {
		this.mediator = mediator;
	}

	//fake implementation
	public void sendPacket(byte[] packet) {
		String strBytes = Base64.getEncoder().encodeToString(packet);
		System.out.println("Sending message - " + strBytes);
		mediator.notifyPacketSentToServer(packet);
	}

}
