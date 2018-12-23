package RequestBuilders;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static Client.Main.client;
import static Client.Main.token;

public class UsersRequestBuilder extends RequestBuilder {
    public UsersRequestBuilder(String query){
        super(query);
    }

    @Override
    public void build() {
        String[] queryParams = query.split(" ");
        if (queryParams.length != 1) {
            System.out.println("USAGE:\n\t/users");
            return;
        }

        Request request = new Request.Builder()
                .url("http://127.0.0.1:8080/users")
                .header("Authorization","Token " + token)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 403) {
                System.out.println("you are not authorized");
                return;
            }

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
