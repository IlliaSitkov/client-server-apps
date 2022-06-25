package exceptions;

public class PacketDuplicatedException extends RuntimeException {

    public PacketDuplicatedException(long packetId) {
        super("Duplicated packet id = "+packetId);
    }
}
