package Server.User;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.HashMap;

public class UserDB {
    private HashMap<String, UserInfo> names = new HashMap<>();
    private HashMap<String, UserInfo> tokens = new HashMap<>();
    private static int id = 1;


    public UserInfo getUserByName(String username) {
        return names.get(username);
    }

    public UserInfo getUserByToken(String token) {
        return tokens.get(token);
    }

    public UserInfo addUser(String username) {
        if (names.containsKey(username))
            return null;
        UserInfo user = new UserInfo();
        user.setUsername(username);
        String token = generateToken();
        user.setToken(token);
        user.setId(id++);
        user.setLastRequestTime(LocalTime.now());
        names.put(username, user);
        tokens.put(token, user);
        return user;
    }

    public UserInfo getUserById(int id) {
        for (HashMap.Entry<String, UserInfo> entry : names.entrySet()) {
            if (entry.getValue().getId() == id)
                return entry.getValue();
        }
        return null;
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        String token = bytes.toString();
        return token;
    }

    public void removeUser(String username) {
        String token = names.get(username).getToken();
        names.remove(username);
        tokens.remove(token);
    }

    public HashMap<String, UserInfo> getNames() {
        return names;
    }

    public HashMap<String, UserInfo> getTokens() {
        return tokens;
    }
}
