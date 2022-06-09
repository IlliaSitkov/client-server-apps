package message.cipher;

import exceptions.CipherException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CipherImpl implements Cipher {

    public enum Algorithm {AES}

    private javax.crypto.Cipher cipher;

    private final Algorithm algorithm;

    private SecretKey secretKey;

    private static final String ENCRYPTION_KEY_STRING =  "abfgdhjutgfHHgGfaKLiOOwqyrNMVCBl";

    public CipherImpl(final Algorithm algorithm) {
        this.algorithm = algorithm;
        initCipher();
        initKey();
    }

    private void initCipher() {
        try {
            cipher = javax.crypto.Cipher.getInstance(algorithm.toString());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void initKey() {
        secretKey = new SecretKeySpec(ENCRYPTION_KEY_STRING.getBytes(), algorithm.toString());
    }

    private void initMode(final int mode){
        try {
            cipher.init(mode, secretKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public final byte[] encrypt(final String string) throws CipherException {
        initMode(javax.crypto.Cipher.ENCRYPT_MODE);
        try {
            return cipher.doFinal(string.getBytes());
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new CipherException(e);
        }
    }

    public final byte[] decrypt(final byte[] bytes) throws CipherException {
        initMode(javax.crypto.Cipher.DECRYPT_MODE);
        try{
            return cipher.doFinal(bytes);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new CipherException(e);
        }
    }


}
