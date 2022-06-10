package packet;

import java.nio.ByteBuffer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import message.Message;
import utils.CRC16Calculator;


@Getter
@Setter
@EqualsAndHashCode
public class Packet {

	public final byte bMagic = 0x13;
	
	public byte bSrc;
	
	public long bPktId;
	
	public int wLen;
	
	public short wCrc16;
	
	public Message bMsg;
	
	public short wMsgCrc16;
	
	public Packet(byte bSrc, long bPktId, int wLen, int cType, int userId, String message) {
		this.bSrc = bSrc;
		this.bPktId = bPktId;
		this.wLen = wLen;
		this.bMsg = new Message(cType, userId, message);
		ByteBuffer buffer =  ByteBuffer.allocate(1 + Long.BYTES + Integer.BYTES);
		buffer.put(this.bSrc);
		buffer.putLong(this.bPktId);
		buffer.putInt(this.wLen);
		this.wCrc16 = (short) CRC16Calculator.getCRC16(buffer.array());
		ByteBuffer bufferMsg = ByteBuffer.allocate(Integer.BYTES * 2 + message.getBytes().length);
		bufferMsg.putInt(cType);
		bufferMsg.putInt(userId);
		bufferMsg.put(message.getBytes());
		this.wMsgCrc16 = (short) CRC16Calculator.getCRC16(bufferMsg.array());		
	}
	
	@Override
    public String toString() {
        return "packet.Packet{" +
                "bSrc='" + this.bSrc + '\'' +
                ", bPktId=" + this.bPktId +
                ", wLen=" + this.wLen +
                ", wCrc16=" + this.wCrc16 + 
                ", bMsg=" + this.bMsg.toString() + 
                ", wMsgCrc16=" + this.wMsgCrc16 +
                '}';
    }
	
	
	
}
