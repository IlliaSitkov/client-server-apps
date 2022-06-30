package exceptions;

public class ServerIsDownException extends RuntimeException {
	
	public ServerIsDownException() {
        super("Server is down!");
    }
	
}
