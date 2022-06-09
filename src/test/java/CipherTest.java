import exceptions.CipherException;
import message.cipher.CipherImpl;
import org.junit.Assert;
import org.junit.Test;

public class CipherTest {

    private final CipherImpl cipher = new CipherImpl(CipherImpl.Algorithm.AES);


    @Test(expected = CipherException.class)
    public void decryptString_whenDistortedBytes_thenExceptionThrown() throws CipherException {
        byte[] encrypted = cipher.encrypt("Some string");
        encrypted[0] = 12;
        cipher.decrypt(encrypted);
    }

    @Test
    public void encryptDecryptString_whenDifferentChars_thenEqualString() throws CipherException {
        String s = "ІіїЇЄҐґфівапролОЗУКАИТБSoMe string  \n1234567890{}[]|\\/?<>.,`~ ::'!@#$%^&*()_+=-";
        byte[] encrypted = cipher.encrypt(s);
        String decrypted = new String(cipher.decrypt(encrypted));
        Assert.assertEquals(s,decrypted);
    }



}
