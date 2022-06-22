package processing;

import java.util.Base64;

public class Sender {

	private static Sender instance;
	
	public static Sender getInstance() {
		if(instance == null)
			instance = new Sender();
		return instance;
	}
	
	//fake implementation
	public void sendPacket(byte[] packet) {
		String strBytes = Base64.getEncoder().encodeToString(packet);
		System.out.println("Sending message - " + strBytes);
	}
	
}
