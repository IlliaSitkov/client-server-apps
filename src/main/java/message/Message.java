package message;

import lombok.Getter;
import org.json.JSONObject;

import java.util.Objects;

@Getter
public class Message {
    public static final int HEADER_LENGTH = Integer.BYTES * 2;
    private final JSONObject messageJSON;
    private final int cType;
    private final int userId;

    public Message( int cType, int userId) {
        this(cType,userId,"{}");
    }

    public Message( int cType, int userId, String message) {
        this.messageJSON = new JSONObject(message);
        this.cType = cType;
        this.userId = userId;
    }

    public void putValue(String key, Object value) {
        messageJSON.put(key, value);
    }

    public void getValue(String key) {
        messageJSON.get(key);
    }

    public String getMessage() {
        return messageJSON.toString();
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + messageJSON + '\'' +
                ", cType=" + cType +
                ", userId=" + userId +
                '}';
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return cType == message.cType && userId == message.userId && getMessage().equals(message.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage(), cType, userId);
    }
}