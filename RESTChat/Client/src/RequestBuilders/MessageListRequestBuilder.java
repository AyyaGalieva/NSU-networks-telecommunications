package RequestBuilders;

import okhttp3.Request;

import static Client.Main.token;

public class MessageListRequestBuilder {
    private Request request;

    public MessageListRequestBuilder(int offset, int count) {
        request = new Request.Builder()
                .url("http://localhost:8080/messages?offset=" + offset + "&count=" + count)
                .addHeader("Authorization", "Token " + token)
                .get()
                .build();
    }

    public Request getRequest() {
        return request;
    }
}
