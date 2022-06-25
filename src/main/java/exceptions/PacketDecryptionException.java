package exceptions;

public class PacketDecryptionException extends RuntimeException{


    public PacketDecryptionException() {
        super("Could not decrypt a packet from bytes");
    }
}
