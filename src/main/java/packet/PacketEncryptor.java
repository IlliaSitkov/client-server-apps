package packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import exceptions.CipherException;
import message.Message;
import message.MessageEncryptor;

public class PacketEncryptor {

	public static byte[] encryptPacket(Packet packet) throws CipherException {
		
		ByteBuffer buffer = ByteBuffer.allocate(Packet.PACKET_CONST_LENGTH + packet.getWLen());
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.put(Packet.bMagic);
		buffer.put(packet.getBSrc());
		buffer.putLong(packet.getBPktId());
		buffer.putInt(packet.getWLen());
		buffer.putShort(packet.getWCrc16());
		buffer.put(MessageEncryptor.encryptMessage(packet.getBMsg()));
		buffer.putShort(packet.getWMsgCrc16());
		return buffer.array();
	}
	
	//this function is able to recognize several glued packets
	public static List<Packet> decryptPacket(byte[] encryptedPacketBytes) throws CipherException {
		ByteBuffer buffer = ByteBuffer.wrap(encryptedPacketBytes);
		buffer.order(ByteOrder.BIG_ENDIAN);
		List<Packet> res = new ArrayList<>();
		int i = 0;
		while(i < encryptedPacketBytes.length) {
			if(encryptedPacketBytes[i] == Packet.bMagic) {
				try {
					byte bsrc = encryptedPacketBytes[++i];
					byte[] longSubArr = new byte[Long.BYTES];
					for(int j = ++i; j < longSubArr.length + i; j++)
						longSubArr[j - i] = encryptedPacketBytes[j];
					long pktid = bytesToLong(longSubArr);
					i += Long.BYTES;
					byte[] intSubArr = new byte[Integer.BYTES];
					for(int j = i; j < intSubArr.length + i; j++)
						intSubArr[j - i] = encryptedPacketBytes[j];
					int wlen = bytesToInt(intSubArr);
					i += Integer.BYTES;
					short crc16 = bytesToShort(new byte[] {encryptedPacketBytes[i], encryptedPacketBytes[++i]});
					byte[] msgSubArr = new byte[wlen];
					for(int j = ++i; j < msgSubArr.length + i; j++)
						msgSubArr[j - i] = encryptedPacketBytes[j];
					Message msg = MessageEncryptor.decryptMessage(msgSubArr);
					i += wlen;
					short msgCrc16 = bytesToShort(new byte[] {encryptedPacketBytes[i], encryptedPacketBytes[++i]});
					Packet packet = new Packet(bsrc, pktid, msg);
					if(crc16 != packet.getWCrc16() || msgCrc16 != packet.getWMsgCrc16())
						return res;
					res.add(packet);
				} catch(ArrayIndexOutOfBoundsException e) {
					return res;
				}
			} else
				i++;
		}
		return res;
	}
	
	private static long bytesToLong(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.put(bytes);
	    buffer.flip();
	    return buffer.getLong();
	}
	
	private static int bytesToInt(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
	    buffer.put(bytes);
	    buffer.flip();
	    return buffer.getInt();
	}
	
	private static short bytesToShort(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
	    buffer.put(bytes);
	    buffer.flip();
	    return buffer.getShort();
	}
	
}
