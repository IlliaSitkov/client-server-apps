package exceptions;

public class InvalidStringException extends RuntimeException {
    public InvalidStringException() {
        super("Invalid string format!");
    }
}
