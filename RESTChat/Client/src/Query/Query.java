package Query;

import RequestBuilders.*;
import okhttp3.Request;

import static Client.Main.token;

public class Query {
    private String query;
    private QueryType type;

    public Query(String query){
        this.query = query;
        detectType();
    }

    private void detectType() {
        String[] args = query.split(" ");
        switch (args[0]) {
            case "/login":
                type = QueryType.LOGIN_QUERY;
                break;
            case "/logout":
                type = QueryType.LOGOUT_QUERY;
                break;
            case "/users":
                type = QueryType.GET_USERLIST_QUERY;
                break;
            case "/user":
                type = QueryType.GET_USERINFO_QUERY;
                break;
            default:
                type = QueryType.POST_MESSAGE_QUERY;
                break;
        }
    }

    public Request createRequest() {
        switch (type) {
            case LOGIN_QUERY:
                if (token != null) {
                    System.out.println("you have already logged in");
                    return null;
                }
                LoginRequestBuilder loginRequestBuilder = new LoginRequestBuilder(query);
                return loginRequestBuilder.getRequest();
            case LOGOUT_QUERY:
                LogoutRequestBuilder logoutRequestBuilder = new LogoutRequestBuilder(query);
                return logoutRequestBuilder.getRequest();
            case POST_MESSAGE_QUERY:
                PostMessageRequestBuilder postMessageRequestBuilder = new PostMessageRequestBuilder(query);
                return postMessageRequestBuilder.getRequest();
            case GET_USERLIST_QUERY:
                UserlistRequestBuilder userlistRequestBuilder = new UserlistRequestBuilder(query);
                return userlistRequestBuilder.getRequest();
            case GET_USERINFO_QUERY:
                UserInfoRequestBuilder userInfoRequestBuilder = new UserInfoRequestBuilder(query);
                return userInfoRequestBuilder.getRequest();
            default:
                return null;
        }
    }

    public QueryType getType() {
        return type;
    }
}
