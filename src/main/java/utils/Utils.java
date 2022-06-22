package utils;

import exceptions.InvalidNumberException;
import exceptions.InvalidStringException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
	
	public static final int THREAD_NUMB = 2;

    public static final long TERMINATION_TIMEOUT_DAYS = 1;
	
    public static long generateId() {
        return (long)(System.currentTimeMillis()*Math.random()+Math.random()*Math.random()*100);
    }

    public static String processString(String string) {
        return string.trim().replaceAll("\\s+", " ");
    }

    public static void validateString(String string, boolean required, int maxLength) {
        boolean res = !required || string.length() > 0;
        res = res && string.length() <= maxLength;
        if (!res) {
            throw new InvalidStringException();
        }
    }

    public static void validateNumber(double number) {
        if (number < 0) {
            throw new InvalidNumberException();
        }
    }

    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> getEmptySynchronizedList() {
        return Collections.synchronizedList(new ArrayList<>());
    }

}
