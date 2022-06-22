package exceptions;

public class NameNotUniqueException extends RuntimeException {
    public NameNotUniqueException(String name) {
        super("Name must be unique! Entity with name = '"+name+"' already exists!");
    }
}
