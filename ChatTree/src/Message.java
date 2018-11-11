import java.io.Serializable;

public class Message implements Serializable {
    private String message;
    private int guid;
    private MessageType type;
    private static int count = 0;

    Message(String msg, String name) {
        this.message = name + ": " + msg;
        this.guid = count++;
    }

    public String getMessage() {
        return message;
    }

    public int getGuid() {
        return guid;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}