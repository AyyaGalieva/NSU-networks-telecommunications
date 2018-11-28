package Server;

import Server.Message.MessageDB;
import Server.User.UserDB;
import io.undertow.Undertow;
import Handlers.ServerHttpHandler;

import java.util.Timer;

public class Main {
    public static UserDB users = new UserDB();
    public static MessageDB messages = new MessageDB();

    public static void main(final String[] args) {
        UserChecker userChecker = new UserChecker();
        new Timer(true).schedule(userChecker, 3000, 3000);

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost", new ServerHttpHandler())
                .build();
        server.start();
    }
}