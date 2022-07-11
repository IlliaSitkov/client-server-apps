package exceptions;

public class InvalidLoginParametersException extends RuntimeException {

	public InvalidLoginParametersException() {
        super("Login or password parameters cannot be blank or null");
    }
	
}
