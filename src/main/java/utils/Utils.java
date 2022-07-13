package utils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import exceptions.InvalidNumberException;
import exceptions.InvalidStringException;
import exceptions.PathVariableNotFound;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return uri.matches(expectedURI+"((/?$)|(/\\?.*)?)");
    }


    public static long getIdFromPath(String path, String basePath) {
        Pattern pattern = Pattern.compile(basePath+"/(\\d+)/?$");
        Matcher matcher = pattern.matcher(path);
        if (matcher.matches()) {
            String match = matcher.group(1);
            return Long.parseLong(match);
        }
        throw new PathVariableNotFound(path);
    }

    public static byte[] getResponseBytes(String key, Object value) {
        JSONObject object = new JSONObject();
        object.put(key, value);
        return object.toString().getBytes();
    }


    public static void sendResponse(HttpExchange exchange, byte[] bytes, int responseCode) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type","application/json");

        addCorsHeaders(exchange);

        exchange.sendResponseHeaders(responseCode, bytes.length);
        OutputStream outputStream = exchange.getResponseBody();

        outputStream.write(bytes);
        outputStream.close();
    }

    public static void sendResponseNoContent(HttpExchange exchange, int responseCode) throws IOException {
        addCorsHeaders(exchange);
        exchange.sendResponseHeaders(responseCode, 0);
    }

    public static JSONObject getRequestBody(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getRequestHeaders();
        int contentLength = Integer.parseInt(headers.getFirst("Content-length"));
        if (contentLength == 0) {
            return new JSONObject();
        }
        InputStream inputStream = exchange.getRequestBody();
        byte[] bodyBytes = new byte[contentLength];
        inputStream.read(bodyBytes);
        return new JSONObject(new String(bodyBytes));
    }

    public static Map<String, String> getQueryParams(String query) {
        if(query == null) {
            return null;
        }
        
        String substr = query.split("\\?")[1];
        Map<String, String> result = new HashMap<>();
        for (String param : substr.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }
    
    public static void processException(HttpExchange exchange, String message, int statusCode) throws IOException {
		System.out.println(message);
        byte[] bytes =  Utils.getResponseBytes("message", message);
        Utils.sendResponse(exchange, bytes, statusCode);
	}


    public static void addCorsHeaders(HttpExchange exchange) {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Access-Control-Allow-Methods", "GET, DELETE, PATCH, PUT, OPTIONS");
        responseHeaders.add("Access-Control-Allow-Headers", "Content-Type, JWToken");
    }


}
