package exceptions;

public class InvalidCommandException extends RuntimeException {

	public InvalidCommandException() {
        super("Command under this number doesn`t exist");
    }
}
