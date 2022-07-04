package server.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import packet.Packet;
import processing.Mediator;

public class TCPClientHandler implements Runnable {
	
	private Socket clientSocket;
	
	private Mediator mediator;
	
	public TCPClientHandler(Socket socket, Mediator mediator) {
		this.clientSocket = socket;
		this.mediator = mediator;
	}
	
	@Override
	public void run() {
		try {
			InputStream inStream = this.clientSocket.getInputStream();
			OutputStream outStream = this.clientSocket.getOutputStream();
			boolean over = false;
			while(!over) {
				byte[] bytes = new byte[Packet.PACKET_MAX_LENGTH];
				int numb = inStream.read(bytes);
				//end of client session
				over = foldsTo(101, bytes) || numb == 0;
				if(over)
					continue;
				//heartbeat message from client
				if(foldsTo(100, bytes)) {
					System.out.println("Server received heartbeat message");
					outStream.write(100);
					continue;
				}
//				List<Packet> packets = PacketEncryptor.decryptPacket(bytes);
//	            if (packets.size() < 1) {
//	                throw new PacketDecryptionException();
//	            }
//	            Packet packet = packets.get(0);
//	            System.out.println("Received packet: " + packet.toString());
//	            Packet res = new Packet(packet.getBSrc(), packet.getBPktId(), new Message(0, 0, "{\"result\":\"OK\"}"));
//	            outStream.write(PacketEncryptor.encryptPacket(res));
				this.mediator.receiveMessage(bytes, outStream);
			}
			
			inStream.close();
			outStream.close();
			this.clientSocket.close();
			
		} catch (IOException e) {
			//throw new RuntimeException(e);
			System.out.println(e.getMessage());
		}
	}
	
	private boolean foldsTo(int val, byte[] arr) {
		int sum = 0;
		for(int i = 0; i < arr.length; i++)
			sum += arr[i];
		return sum == val;
	}
	
}
