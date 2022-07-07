package utils;

import exceptions.InvalidNumberException;
import exceptions.InvalidStringException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

    private Utils() {
        throw new RuntimeException("Can not create object");
    }
	
	public static final int THREAD_NUMB = 3;

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


    public static long getPacketId(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getLong(2);
    }


    public static boolean exactURI(String uri, String expectedURI) {
        return uri.matches(expectedURI+"$");
    }


    public static long getIdFromPath(String path, String basePath) {
        String[] strings = path.split(basePath);
        return Long.parseLong(strings[1]);
    }






}
