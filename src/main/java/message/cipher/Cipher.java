package message.cipher;

import exceptions.CipherException;

public interface Cipher {

    byte[] encrypt(final String string) throws CipherException;
    byte[] decrypt(final byte[] bytes) throws CipherException;

}
