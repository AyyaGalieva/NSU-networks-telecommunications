package Client;

import Query.Query;
import ResponseHandlers.MessageChecker;
import ResponseHandlers.ResponseHandler;
import okhttp3.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;

public class Main {
    public static String token;
    public static OkHttpClient client = new OkHttpClient();
    public static Timer timer;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while(true) {
            Query query = new Query(sc.nextLine());
            Request request = query.createRequest();

            if (request == null)
                continue;
            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                ResponseHandler responseHandler = new ResponseHandler(response, query.getType());
                responseHandler.handleResponse();

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
