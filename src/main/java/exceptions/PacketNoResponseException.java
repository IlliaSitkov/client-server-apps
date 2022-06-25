package exceptions;

public class PacketNoResponseException extends RuntimeException{


    public PacketNoResponseException() {
        super("No packet was fetched as a response");
    }
}
