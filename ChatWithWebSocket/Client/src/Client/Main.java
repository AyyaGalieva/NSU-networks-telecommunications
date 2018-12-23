package Client;

import RequestBuilders.LoginRequestBuilder;
import RequestBuilders.LogoutRequestBuilder;
import RequestBuilders.MessageRequestBuilder;
import RequestBuilders.UsersRequestBuilder;
import okhttp3.*;

import java.util.Scanner;

public class Main {
    public static String token;
    public static OkHttpClient client = new OkHttpClient();
    public static OkHttpClient connectionClient = new OkHttpClient();
    public static WebSocket webSocket;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while(true) {
            String query = sc.nextLine();
            String[] strs = query.split(" ");
            switch (strs[0]) {
                case "/login":
                    LoginRequestBuilder loginRequestBuilder = new LoginRequestBuilder(query);
                    loginRequestBuilder.build();
                    break;
                case "/logout":
                    LogoutRequestBuilder logoutRequestBuilder = new LogoutRequestBuilder(query);
                    logoutRequestBuilder.build();
                    break;
                case "/users":
                    UsersRequestBuilder usersRequestBuilder = new UsersRequestBuilder(query);
                    usersRequestBuilder.build();
                    break;
                default:
                    MessageRequestBuilder messageRequestBuilder = new MessageRequestBuilder(query);
                    messageRequestBuilder.build();
                    break;
            }
        }
    }
}
