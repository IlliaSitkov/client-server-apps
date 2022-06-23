package message;

import exceptions.CipherException;
import message.cipher.Cipher;
import message.cipher.CipherImpl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MessageEncryptor {

    private MessageEncryptor(){}

    private static final Cipher cipher = new CipherImpl(CipherImpl.Algorithm.AES);

    public static byte[] encryptMessage(Message message) throws CipherException {
        byte[] encryptedMessageTextBytes = encryptMessageText(message.getMessage());
        ByteBuffer buffer = ByteBuffer.allocate(encryptedMessageTextBytes.length+Message.HEADER_LENGTH);
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.putInt(message.getCType());
        buffer.putInt(message.getUserId());
        buffer.put(encryptedMessageTextBytes);

        return buffer.array();
    }

    public static Message decryptMessage(byte[] encryptedMessageBytes) throws CipherException {
        ByteBuffer buffer = ByteBuffer.wrap(encryptedMessageBytes);
        buffer.order(ByteOrder.BIG_ENDIAN);

        int cType = buffer.getInt();
        int userId = buffer.getInt();

        byte[] encryptedMessageTextBytes = new byte[buffer.capacity()-Message.HEADER_LENGTH];
        buffer.get(encryptedMessageTextBytes,0,encryptedMessageTextBytes.length);

        String message = decryptMessageText(encryptedMessageTextBytes);

        return new Message(cType,userId,message);
    }

    private static byte[] encryptMessageText(String message) throws CipherException {
        return cipher.encrypt(message);
    }
    private static String decryptMessageText(byte[] encryptedMessageTextBytes) throws CipherException {
        return new String(cipher.decrypt(encryptedMessageTextBytes));
    }

}
