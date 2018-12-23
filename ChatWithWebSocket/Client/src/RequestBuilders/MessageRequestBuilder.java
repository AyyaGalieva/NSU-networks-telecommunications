package RequestBuilders;

import okhttp3.WebSocket;
import org.json.simple.JSONObject;

import static Client.Main.token;
import static Client.Main.webSocket;

public class MessageRequestBuilder extends RequestBuilder {
    public MessageRequestBuilder(String query) {
        super(query);
    }

    @Override
    public void build() {
        if (token == null) {
            System.out.println("you are not authorized");
            return;
        }

        JSONObject json = new JSONObject();
        json.put("message", query);
        webSocket.send(json.toString());
    }
}
