package exceptions;

public class GroupNotFoundException extends RuntimeException {
    public GroupNotFoundException(long id) {
        super("Group with id = "+id+" not found");
    }
}
