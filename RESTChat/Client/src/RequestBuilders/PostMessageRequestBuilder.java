package RequestBuilders;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.simple.JSONObject;

import static Client.Main.token;

public class PostMessageRequestBuilder {
    private Request request;
    private RequestBody body;
    private JSONObject json = new JSONObject();

    public PostMessageRequestBuilder(String query) {
        json.put("message", query);
        body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        request = new Request.Builder()
                .url("http://localhost:8080/messages")
                .addHeader("Authorization", "Token " + token)
                .post(body)
                .build();
    }

    public Request getRequest() {
        return request;
    }
}
