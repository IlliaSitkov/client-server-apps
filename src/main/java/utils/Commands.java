package utils;

import java.util.Arrays;
import java.util.Optional;

public enum Commands {
    PRODUCT_GET_QUANTITY(0),
    PRODUCT_TAKE_QUANTITY(1),
    PRODUCT_ADD_QUANTITY(2),
    PRODUCT_CREATE(3),
    PRODUCT_SET_PRICE(4),
    GROUP_CREATE(5);
    
    private final int value;
      
    private Commands(int value) {
    	this.value = value;
    }
    
    public static Optional<Commands> valueOf(int value) {
        return Arrays.stream(values())
            .filter(command -> command.value == value)
            .findFirst();
    }
}
