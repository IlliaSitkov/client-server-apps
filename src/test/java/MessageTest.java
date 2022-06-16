import exceptions.CipherException;
import message.Message;
import message.MessageEncryptor;
import message.cipher.Cipher;
import message.cipher.CipherImpl;
import org.junit.Assert;
import org.junit.Test;

public class MessageTest {

    private final Cipher cipher = new CipherImpl(CipherImpl.Algorithm.AES);

    @Test
    public void encryptMessages_whenTwoEqualMessages_thenShouldBeEqual() throws CipherException {
        Message m1 = new Message(12,123,"{\"key\":\"Message\"}");
        byte[] encryptedM1 = MessageEncryptor.encryptMessage(m1);

        Message m2 = MessageEncryptor.decryptMessage(encryptedM1);
        Assert.assertEquals(m1,m2);
    }

    @Test
    public void encryptMessages_whenEncryptedMessageChanged_thenShouldNotBeEqual() throws CipherException {
        Message m1 = new Message(12,123,"{\"key\":\"Message\"}");
        byte[] bs = MessageEncryptor.encryptMessage(m1);
        bs[1] = 13;
        Message m2 = MessageEncryptor.decryptMessage(bs);

        Assert.assertNotEquals(m1,m2);
    }

    @Test
    public void encryptMessage_whenCorrectMessage_thenBytesLengthShouldBeEqualToExpectedLength() throws CipherException {
        String s = "{\"key\":\"Message\"}";
        byte[] encipheredText = cipher.encrypt(s);
        int expectedLength = encipheredText.length+Message.HEADER_LENGTH;
        Message m1 = new Message(12,123,s);

        Assert.assertEquals(expectedLength, MessageEncryptor.encryptMessage(m1).length);
    }


}
