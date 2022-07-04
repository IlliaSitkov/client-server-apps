package server.tcp;

import java.io.IOException;

import exceptions.CipherException;
import message.Message;
import packet.Packet;

public class MainClientTCP {

	public static void main(String[] args) throws IOException, CipherException {
		StoreClientTCP client1 = new StoreClientTCP();
		
		client1.fetch(new Packet((byte)132, 123, new Message(8,8,"{\"result\":\"Foo\"}")));
		
		client1.close();
		
	}

}
