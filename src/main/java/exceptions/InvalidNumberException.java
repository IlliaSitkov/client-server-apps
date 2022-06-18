package exceptions;

public class InvalidNumberException extends RuntimeException {

    public InvalidNumberException() {
        super("Number must be greater than zero");
    }
}
