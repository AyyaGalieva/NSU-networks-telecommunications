package Handlers;

import Server.User.UserInfo;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.ByteBuffer;

import static Server.Main.users;

public class LoginHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange hse) {
        if (hse.getRequestMethod().toString().equals("POST")) {
            JSONParser parser = new JSONParser();
            String request = getRequest(hse);
            try {
                JSONObject json = (JSONObject) parser.parse(request);
                String username = json.get("username").toString();
                UserInfo user = users.addUser(username);
                if (user == null) {
                    hse.setStatusCode(400);
                    hse.getResponseHeaders().put(Headers.WWW_AUTHENTICATE, "Token ream='username already exists'");
                } else {
                    hse.setStatusCode(200);
                    hse.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    JSONObject response = new JSONObject();
                    response.put("id", users.getUserByName(username).getId());
                    response.put("username", username);
                    response.put("token", users.getUserByName(username).getToken());
                    hse.getResponseSender().send(response.toString());
                }
            } catch (ParseException e) {
                hse.setStatusCode(400);
            }
        } else
            hse.setStatusCode(405);
    }

    private String getRequest(HttpServerExchange hse) {
        String request = null;
        try {
            ByteBuffer buf = ByteBuffer.allocate((int) hse.getRequestContentLength());
            hse.getRequestChannel().read(buf);
            request = new String(buf.array());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return request;
    }
}
