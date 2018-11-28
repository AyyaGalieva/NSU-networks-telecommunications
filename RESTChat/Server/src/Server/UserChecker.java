package Server;

import Server.User.UserInfo;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.TimerTask;

import static Server.Main.messages;
import static Server.Main.users;


public class UserChecker extends TimerTask {

    @Override
    public void run() {
        for (HashMap.Entry<String, UserInfo> entry : users.getNames().entrySet()) {
            if (LocalTime.now().getSecond() - entry.getValue().getLastRequestTime().getSecond() > 3) {
                messages.postMessage(entry.getValue().getId(), "*timeout logout*");
                users.removeUser(entry.getValue().getUsername());
            }
        }
    }
}
