package Server.Message;

import java.util.ArrayList;

public class MessageDB {
    private static int id = 0;
    private ArrayList<MessageInfo> msgArray = new ArrayList<>();

    public ArrayList<MessageInfo> getMsgArray() {
        return msgArray;
    }

    public int postMessage(int author, String message) {
        MessageInfo msg = new MessageInfo();
        msg.setId(id++);
        msg.setAuthor(author);
        msg.setMessage(message);
        msgArray.add(msg);
        return id-1;
    }

    public ArrayList<MessageInfo> getMessages(int offset, int count) {
        ArrayList<MessageInfo> messages = new ArrayList<>();
        for (MessageInfo messageInfo : msgArray) {
            if (messageInfo.getId() < offset)
                continue;

            if (messageInfo.getId()-offset > count)
                break;

            messages.add(messageInfo);
        }
        return messages;
    }
}
