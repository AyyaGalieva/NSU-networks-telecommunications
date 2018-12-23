package Handlers;

import Server.User.UserInfo;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.LocalTime;
import java.util.HashMap;

import static Server.Main.users;

public class UsersHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange hse) {
        if (hse.getRequestMethod().toString().equals("GET")) {
            try {
                String token = getToken(hse);
                if (users.getUserByToken(token)!=null) {
                    users.getUserByToken(token).setLastRequestTime(LocalTime.now());
                    String[] strs = hse.getRelativePath().split("/");
                    hse.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    JSONObject json = new JSONObject();
                    if (strs.length == 2) {
                        UserInfo user = users.getUserById(Integer.parseInt(strs[1]));
                        json.put("id", user.getId());
                        json.put("username", user.getUsername());
                        hse.getResponseSender().send(json.toString());
                    } else {
                        JSONArray userArray = new JSONArray();
                        for (HashMap.Entry<String, UserInfo> entry: users.getNames().entrySet()){
                            JSONObject userJson = new JSONObject();
                            userJson.put("id", entry.getValue().getId());
                            userJson.put("username", entry.getValue().getUsername());
                            userJson.put("token", entry.getValue().getToken());
                            userArray.add(userJson);
                        }
                        json.put("users", userArray);
                        hse.getResponseSender().send(json.toString());
                    }
                } else
                    hse.setStatusCode(403);
            }catch (Exception e) {
                hse.setStatusCode(400);
            }
        } else
            hse.setStatusCode(405);
    }
    private String getToken(HttpServerExchange hse) throws Exception {
        String token = hse.getRequestHeaders().get("Authorization").element();
        String[] strs = token.split(" ");
        if (strs[0].equals("Token"))
            return strs[1];
        else throw new Exception();
    }
}
