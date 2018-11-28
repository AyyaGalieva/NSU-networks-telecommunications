package RequestBuilders;

import okhttp3.Request;

import static Client.Main.token;

public class UserInfoRequestBuilder {
    private Request request;

    public UserInfoRequestBuilder(String query) {
        String[] queryParams = query.split(" ");
        if (queryParams.length != 2) {
            System.out.println("USAGE:\n\t/user <id>");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(queryParams[1]);
        } catch (NumberFormatException e) {
            System.out.println("USAGE:\n\t/user <id>");
            return;
        }
        request = new Request.Builder()
                .url("http://localhost:8080/users/" + id)
                .addHeader("Authorization", "Token " + token)
                .get()
                .build();
    }

    public Request getRequest() {
        return request;
    }
}
