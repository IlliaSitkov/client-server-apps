package packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import exceptions.CipherException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import message.Message;
import message.MessageEncryptor;
import utils.CRC16Calculator;


@Getter
@Setter
@EqualsAndHashCode
public class Packet {

	public static final int PACKET_MAX_LENGTH = 1024;
	public static final int PACKET_CONST_LENGTH = Long.BYTES +
													Integer.BYTES +
													Short.BYTES +
													Short.BYTES + 2;
	public static final byte bMagic = 0x13;
	
	private byte bSrc;
	
	private long bPktId;
	
	private int wLen;
	
	private short wCrc16;
	
	private Message bMsg;
	
	private short wMsgCrc16;
	
	public Packet(byte bSrc, long bPktId, Message message) {
		this(bSrc, bPktId, message.getCType(), message.getUserId(), message.getMessage());
	}
	
	public Packet(byte bSrc, long bPktId, int cType, int userId, String message) {
		this.bSrc = bSrc;
		this.bPktId = bPktId;
		this.bMsg = new Message(cType, userId, message);
		ByteBuffer buffer =  ByteBuffer.allocate(1 + Long.BYTES + Integer.BYTES);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.put(this.bSrc);
		buffer.putLong(this.bPktId);
		buffer.putInt(this.wLen);
		this.wCrc16 = (short) CRC16Calculator.getCRC16(buffer.array());
		try {
			byte[] msgBytes = MessageEncryptor.encryptMessage(this.bMsg);
			this.wLen = msgBytes.length;
			this.wMsgCrc16 = (short) CRC16Calculator.getCRC16(msgBytes);
		} catch (CipherException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
    public String toString() {
        return "Packet{" +
                "bSrc='" + this.bSrc + '\'' +
                ", bPktId=" + this.bPktId +
                ", wLen=" + this.wLen +
                ", wCrc16=" + this.wCrc16 + 
                ", bMsg=" + this.bMsg.toString() + 
                ", wMsgCrc16=" + this.wMsgCrc16 +
                '}';
    }
	
	
	
}
