package exceptions;

public class ServerAlreadyStoppedException extends RuntimeException {

    public ServerAlreadyStoppedException() {
        super("Server has already been stopped!");
    }
}
