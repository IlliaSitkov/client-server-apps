package exceptions;

public class IdNotUniqueException extends RuntimeException {


    public IdNotUniqueException(Long id) {
        super("Entity with id = "+id+" already exists");
    }
}
