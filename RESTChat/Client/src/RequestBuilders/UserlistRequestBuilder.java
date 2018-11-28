package RequestBuilders;

import okhttp3.Request;

import static Client.Main.token;

public class UserlistRequestBuilder {
    private Request request;

    public UserlistRequestBuilder(String query) {
        String[] queryParams = query.split(" ");
        if (queryParams.length != 1) {
            System.out.println("USAGE:\n\t/users");
            return;
        }

        request = new Request.Builder()
                .url("http://localhost:8080/users")
                .addHeader("Authorization", "Token " + token)
                .get()
                .build();
    }

    public Request getRequest() {
        return request;
    }
}
