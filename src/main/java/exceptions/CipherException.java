package exceptions;

public class CipherException extends Exception {

    public CipherException(Exception e) {
        super(e);
    }
}
