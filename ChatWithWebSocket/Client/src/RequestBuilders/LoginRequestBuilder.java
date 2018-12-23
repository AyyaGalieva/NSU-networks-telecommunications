package RequestBuilders;

import WSListeners.MessageReceiver;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static Client.Main.client;
import static Client.Main.connectionClient;
import static Client.Main.token;
import static Client.Main.webSocket;

public class LoginRequestBuilder extends RequestBuilder{
    public LoginRequestBuilder(String query) {
        super(query);
    }

    @Override
    public void build() {
        if (token != null) {
            System.out.println("you are already logged in");
            return;
        }
        String[] queryParams = query.split(" ");
        if (queryParams.length != 2) {
            System.out.println("USAGE:\n\t/login <username>");
            return;
        }
        JSONObject json = new JSONObject();
        json.put("username", queryParams[1]);
        Request request = new Request.Builder()
                .url("http://127.0.0.1:8080/login")
                .post(RequestBody.create(MediaType.parse("application/json"), json.toString()))
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 400) {
                System.out.println("username already exists");
                return;
            }

            try {
                String respBody = response.body().string();
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(new String(respBody.getBytes(), StandardCharsets.UTF_8));
                token = jsonObject.get("token").toString();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            } catch (ParseException e) {
                System.err.println(e.getMessage());
            }

            connect();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void connect() {
        Request request = new Request.Builder()
                .url("ws://localhost:8080/messages")
                .get()
                .header("Authorization","Token " + token)
                .build();
        MessageReceiver messageReceiver = new MessageReceiver();
        webSocket = connectionClient.newWebSocket(request, messageReceiver);
    }
}
