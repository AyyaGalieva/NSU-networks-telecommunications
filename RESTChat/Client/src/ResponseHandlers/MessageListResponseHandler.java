package ResponseHandlers;

import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MessageListResponseHandler extends ResponseHandler {
    public MessageListResponseHandler(Response response) {
        this.response = response;
    }

    public void handleResponse(MessageChecker messageChecker) {
        try {
            String respBody = response.body().string();
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new String(respBody.getBytes(), StandardCharsets.UTF_8));
            JSONArray messages = (JSONArray)json.get("messages");
            for (int i = 0; i < messages.size(); ++i) {
                JSONObject message = (JSONObject)messages.get(i);
                System.out.println(message.get("author") + ":\n\t" + message.get("message"));
            }
            messageChecker.setOffset(messageChecker.getOffset() + messages.size());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
    }
}
