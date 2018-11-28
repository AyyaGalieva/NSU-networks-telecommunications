package ResponseHandlers;

import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Timer;

import static Client.Main.timer;
import static Client.Main.token;

public class LoginResponseHandler extends ResponseHandler {

    public LoginResponseHandler(Response response) {
        this.response = response;
    }

    @Override
    public void handleResponse() {
        if (response.code() == 500) {
            System.out.println("username is already exist");
            return;
        }
        try {
            String respBody = response.body().string();
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new String(respBody.getBytes(), StandardCharsets.UTF_8));
            token = json.get("token").toString();
            MessageChecker messageChecker = new MessageChecker();
            timer = new Timer(true);
            timer.schedule(messageChecker, 1000, 1000);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }
}
