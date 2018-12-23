package Handlers;

import Server.Message.MessageInfo;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static Server.Main.messages;
import static Server.Main.users;

public class MessagesHandler implements WebSocketConnectionCallback {
    @Override
    public void onConnect(WebSocketHttpExchange webSocketHttpExchange, WebSocketChannel channel) {
        String token = getToken(webSocketHttpExchange);
        int userId = users.getUserByToken(token).getId();
        JSONObject json = new JSONObject();
        JSONArray messagesArray = new JSONArray();
        ArrayList<MessageInfo> messageInfos = messages.getMessages(0, messages.getMsgArray().size());
        for (MessageInfo messageInfo : messageInfos) {
            JSONObject msgJson = new JSONObject();
            msgJson.put("id", messageInfo.getId());
            msgJson.put("message", messageInfo.getMessage());
            msgJson.put("author", messageInfo.getAuthor());
            messagesArray.add(msgJson);
        }
        json.put("messages", messagesArray);
        String messageList = (messageInfos==null)?"":json.toString();
        WebSockets.sendText(messageList, channel, null);

        channel.getReceiveSetter().set(new AbstractReceiveListener() {

            @Override
            protected void onFullTextMessage(WebSocketChannel webSocketChannel, BufferedTextMessage msg) {
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(new String(msg.getData().getBytes(), StandardCharsets.UTF_8));
                    String message = json.get("message").toString();
                    int id = messages.postMessage(userId, message);
                    JSONObject jsonObject = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    JSONObject messageJson = new JSONObject();
                    messageJson.put("id", id);
                    messageJson.put("message", message);
                    messageJson.put("author", userId);
                    jsonArray.add(messageJson);
                    jsonObject.put("messages", jsonArray);

                    for(WebSocketChannel webSocketChannels : channel.getPeerConnections()) {
                        WebSockets.sendText(jsonObject.toString(), webSocketChannels, null);
                    }
                } catch (ParseException e) {
                    System.err.println(e.getMessage());
                }
            }
        });
        channel.resumeReceives();
    }

    private String getToken(WebSocketHttpExchange webSocketHttpExchange) {
        String str = webSocketHttpExchange.getRequestHeaders().get("Authorization").get(0);
        String[] strs = str.split(" ");
        if (strs[0].equals("Token"))
            return strs[1];
        else return null;
    }
}
