package exceptions;

public class NameNotUniqueException extends RuntimeException {
    public NameNotUniqueException() {
        super("Name must be unique!");
    }
}
