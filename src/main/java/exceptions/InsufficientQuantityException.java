package exceptions;

public class InsufficientQuantityException extends RuntimeException {

    public InsufficientQuantityException(int available, int requested) {
        super("Insufficient qunatity: available = "+ available+", requested = "+requested);
    }
}
