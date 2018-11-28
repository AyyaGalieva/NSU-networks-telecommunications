package ResponseHandlers;

import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UserlistResponseHandler extends ResponseHandler {
    public UserlistResponseHandler(Response response) {
        this.response = response;
    }

    @Override
    public void handleResponse() {
        if (response.code() == 403) {
            System.out.println("you are not authorized");
            return;
        }

        try {
            String respBody = response.body().string();
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new String(respBody.getBytes(), StandardCharsets.UTF_8));
            JSONArray users = (JSONArray)json.get("users");
            System.out.println("Users:");
            for (int i = 0; i < users.size(); ++i) {
                JSONObject user = (JSONObject)users.get(i);
                System.out.println(user.get("id") + ": " + user.get("username"));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }
}
