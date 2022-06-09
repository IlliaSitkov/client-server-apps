package message;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Message {
    public static final int HEADER_LENGTH = Integer.BYTES * 2;
    private final String message;
    private final int cType;
    private final int userId;


    public Message( int cType, int userId, String message) {
        this.message = message;
        this.cType = cType;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "message.Message{" +
                "message='" + message + '\'' +
                ", cType=" + cType +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return cType == message1.cType && userId == message1.userId && Objects.equals(message, message1.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, cType, userId);
    }
}