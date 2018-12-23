package RequestBuilders;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

import static Client.Main.client;
import static Client.Main.connectionClient;
import static Client.Main.token;

public class LogoutRequestBuilder extends RequestBuilder{
    public LogoutRequestBuilder(String query) {
        super(query);
    }

    @Override
    public void build() {
        String[] queryParams = query.split(" ");
        if (queryParams.length != 1) {
            System.out.println("USAGE:\n\t/logout");
            return;
        }

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8080/logout")
                .post(RequestBody.create(MediaType.parse("application/json"), ""))
                .header("Authorization","Token " + token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 403) {
                System.out.println("you are not authorized");
                return;
            }
            System.out.println("bye!");
            token = null;
            connectionClient.dispatcher().executorService().shutdown();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
