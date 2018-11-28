package RequestBuilders;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import static Client.Main.token;

public class LogoutRequestBuilder{
    private Request request;
    private RequestBody body;

    public LogoutRequestBuilder(String query) {
        String[] queryParams = query.split(" ");
        if (queryParams.length != 1) {
            System.out.println("USAGE:\n\t/logout");
            return;
        }
        body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
        request = new Request.Builder()
                .url("http://localhost:8080/logout")
                .addHeader("Authorization", "Token " + token)
                .post(body)
                .build();
    }

    public Request getRequest() {
        return request;
    }
}
