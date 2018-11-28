package ResponseHandlers;

import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PostMessageResponseHandler extends ResponseHandler {
    public PostMessageResponseHandler(Response response) {
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
            int id = Integer.parseInt(json.get("id").toString());
            String msg = json.get("message").toString();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }
}
