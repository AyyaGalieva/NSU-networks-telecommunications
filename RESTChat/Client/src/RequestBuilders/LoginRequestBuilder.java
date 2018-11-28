package RequestBuilders;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.simple.JSONObject;

public class LoginRequestBuilder{
    private JSONObject json = new JSONObject();
    private RequestBody body;
    private Request request;

    public LoginRequestBuilder(String query) {
        String[] queryParams = query.split(" ");
        if (queryParams.length != 2) {
            System.out.println("USAGE:\n\t/login <username>");
            return;
        }
        json.put("username", queryParams[1]);
        body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        request = new Request.Builder()
                .url("http://localhost:8080/login")
                .post(body)
                .build();
    }

    public Request getRequest() {
        return request;
    }
}
