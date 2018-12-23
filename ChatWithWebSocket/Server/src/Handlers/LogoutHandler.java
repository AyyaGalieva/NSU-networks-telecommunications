package Handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.websockets.core.WebSocketChannel;
import org.json.simple.JSONObject;

import java.time.LocalTime;

import static Server.Main.messages;
import static Server.Main.users;

public class LogoutHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange hse) {
        if (hse.getRequestMethod().toString().equals("POST")) {
            try {
                String token = getToken(hse);
                if (users.getUserByToken(token) != null) {
                    users.getUserByToken(token).setLastRequestTime(LocalTime.now());
                    messages.postMessage(users.getUserByToken(token).getId(), "*logged out*");
                    users.removeUser(users.getUserByToken(token).getUsername());
                    hse.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    hse.setStatusCode(200);
                    JSONObject response = new JSONObject();
                    response.put("message", "bye!");
                    hse.getResponseSender().send(response.toString());
                } else
                    hse.setStatusCode(403);
            } catch (Exception e) {
                hse.setStatusCode(400);
            }
        } else {
            hse.setStatusCode(405);
        }
    }

    private String getToken(HttpServerExchange hse) throws Exception {
        String token = hse.getRequestHeaders().get("Authorization").element();
        String[] strs = token.split(" ");
        if (strs[0].equals("Token"))
            return strs[1];
        else throw new Exception();
    }
}
