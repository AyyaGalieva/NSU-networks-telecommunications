package WSListeners;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.charset.StandardCharsets;

public class MessageReceiver extends WebSocketListener {
    @Override
    public void onMessage(WebSocket webSocket, String message) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(new String(message.getBytes(), StandardCharsets.UTF_8));
            JSONArray messages = (JSONArray) json.get("messages");
            for (int i = 0; i < messages.size(); ++i) {
                JSONObject msg = (JSONObject) messages.get(i);
                System.out.println(msg.get("author") + ":\n\t" + msg.get("message"));
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        System.out.println(t.getMessage());
    }
}
