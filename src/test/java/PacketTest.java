import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import exceptions.CipherException;
import message.Message;
import message.MessageEncryptor;
import packet.Packet;
import packet.PacketEncryptor;

public class PacketTest {

	@Test
	public void encryptPackets_whenDecryptedPacket_thenShouldBeEqual() throws CipherException {
		Packet p1 = new Packet((byte)2, 3245L, 5, 1, "Hello world");
		var encryptedPacket = PacketEncryptor.encryptPacket(p1);
		List<Packet> list = PacketEncryptor.decryptPacket(encryptedPacket);
		Assert.assertEquals(list.size(), 1);
		Assert.assertEquals(p1, list.get(0));
	}
	
	@Test
	public void encryptTwoPackets_whenByteArraysGlued_thenListSizeShouldBeTwo() throws CipherException {
		Packet p1 = new Packet((byte)2, 3245L, 5, 1, "Hello world");
		Packet p2 = new Packet((byte)12, 10234L, 10, 2, "Hi there!");
		var packetBytes1 = PacketEncryptor.encryptPacket(p1);
		var packetBytes2 = PacketEncryptor.encryptPacket(p2);
		byte[] resArray = new byte[packetBytes1.length + packetBytes2.length];
		System.arraycopy(packetBytes1, 0, resArray, 0, packetBytes1.length);  
    	System.arraycopy(packetBytes2, 0, resArray, packetBytes1.length, packetBytes2.length);  
    	List<Packet> list = PacketEncryptor.decryptPacket(resArray);
    	Assert.assertEquals(list.size(), 2);
    	Assert.assertEquals(list.get(0), p1);
    	Assert.assertEquals(list.get(1), p2);
	}
	
	@Test
	public void encryptPacket_whenByteArrayChanged_thenPacketDropped() throws CipherException {
		Packet p1 = new Packet((byte)2, 3245L, 5, 1, "Hello world");
		var packetBytes = PacketEncryptor.encryptPacket(p1);
		packetBytes[9] = 0;
		List<Packet> list = PacketEncryptor.decryptPacket(packetBytes);
		Assert.assertTrue(list.isEmpty());
	}
	
	@Test
	public void encryptPacket_whenPacketWLen_thenByteArrayLengthEqualToExpected() throws CipherException {
		Packet p1 = new Packet((byte)2, 3245L, 5, 1, "Hello world");
		var packetBytes = PacketEncryptor.encryptPacket(p1);
		Assert.assertEquals(packetBytes.length, Packet.PACKET_CONST_LENGTH + p1.getWLen());
	}
	
	@Test
	public void encryptPacket_whenEncryptedMessageByteArray_thenPacketByteSubarrayEqualToEncryptedMessageByteArray() throws CipherException {
		Message msg = new Message(5, 1, "Hello world");
		Packet p1 = new Packet((byte)2, 3245L, msg);
		var packetBytes = PacketEncryptor.encryptPacket(p1);
		byte[] packetMsgSubArray = new byte[p1.getWLen()];
		System.arraycopy(packetBytes, 16, packetMsgSubArray, 0, packetMsgSubArray.length);
		var msgBytes = MessageEncryptor.encryptMessage(msg);
		Assert.assertArrayEquals(packetMsgSubArray, msgBytes);
	}
}
