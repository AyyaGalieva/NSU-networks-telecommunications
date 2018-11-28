package ResponseHandlers;

import Query.QueryType;
import okhttp3.Response;

public class ResponseHandler {
    protected Response response;
    protected QueryType type;

    public ResponseHandler(){}

    public ResponseHandler(Response response, QueryType type) {
        this.response = response;
        this.type = type;
    }

    public void handleResponse() {
        switch (type) {
            case LOGIN_QUERY:
                LoginResponseHandler loginResponseHandler = new LoginResponseHandler(response);
                loginResponseHandler.handleResponse();
                break;
            case LOGOUT_QUERY:
                LogoutResponseHandler logoutResponseHandler = new LogoutResponseHandler(response);
                logoutResponseHandler.handleResponse();
                break;
            case GET_MESSAGELIST_QUERY:
                MessageListResponseHandler messageListResponseHandler = new MessageListResponseHandler(response);
                messageListResponseHandler.handleResponse();
                break;
            case POST_MESSAGE_QUERY:
                PostMessageResponseHandler postMessageResponseHandler = new PostMessageResponseHandler(response);
                postMessageResponseHandler.handleResponse();
                break;
            case GET_USERLIST_QUERY:
                UserlistResponseHandler userlistResponseHandler = new UserlistResponseHandler(response);
                userlistResponseHandler.handleResponse();
                break;
            case GET_USERINFO_QUERY:
                UserInfoResponseHandler userInfoResponseHandler = new UserInfoResponseHandler(response);
                userInfoResponseHandler.handleResponse();
                break;
        }
    }
}
