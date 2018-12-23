package Server;

import Handlers.LoginHandler;
import Handlers.LogoutHandler;
import Handlers.MessagesHandler;
import Handlers.UsersHandler;
import Server.Message.MessageDB;
import Server.User.UserDB;
import io.undertow.Undertow;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.websocket;

public class Main {
    public static UserDB users = new UserDB();
    public static MessageDB messages = new MessageDB();

    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addListener(8080, "localhost")
                .setHandler(
                        path()
                                .addPrefixPath("/login", new LoginHandler())
                                .addPrefixPath("/logout", new LogoutHandler())
                                .addPrefixPath("/users", new UsersHandler())
                                .addPrefixPath("/messages", websocket(new MessagesHandler()))
                ).build();
        server.start();
    }
}
