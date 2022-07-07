package exceptions;

public class PathVariableNotFound extends RuntimeException {


    public PathVariableNotFound(String route) {
        super("Path variable not found: "+route);
    }
}
