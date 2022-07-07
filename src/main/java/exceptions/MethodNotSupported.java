package exceptions;

public class MethodNotSupported extends RuntimeException {

    public MethodNotSupported(String method, String path) {
        super("HTTP Method "+method+" not supported on path "+path);
    }
}
