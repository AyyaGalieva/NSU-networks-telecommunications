package Server.User;

import java.time.LocalTime;

public class UserInfo {
    private String username;
    private int id;
    private String token;
    private LocalTime lastRequestTime;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalTime getLastRequestTime() {
        return lastRequestTime;
    }
    public void setLastRequestTime(LocalTime lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }
}
