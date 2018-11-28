package Handlers;

import Requests.Request;
import Responses.Response;
import Responses.errors.*;
import io.undertow.util.HeaderValues;
import java.util.Arrays;
import java.util.List;
import static Server.Main.users;

public class RequestHandler {

    protected Request request;
    protected String token;

    public RequestHandler(Request request) {
        this.request = request;
    }

    public RequestHandler(){}

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response handleRequest() {
        switch (request.getType()) {
            case LOGIN_REQUEST:
                LoginHandler loginHandler = new LoginHandler();
                loginHandler.setRequest(request);
                return loginHandler.handleRequest();
            case LOGOUT_REQUEST:
                LogoutHandler logoutHandler = new LogoutHandler();
                logoutHandler.setRequest(request);
                return logoutHandler.handleRequest();
            case GET_USERLIST_REQUEST:
                UserListHandler userListHandler = new UserListHandler();
                userListHandler.setRequest(request);
                return userListHandler.handleRequest();
            case GET_USER_INFO:
                UserInfoHandler userInfoHandler = new UserInfoHandler();
                userInfoHandler.setRequest(request);
                return userInfoHandler.handleRequest();
            case GET_MESSAGESLIST_REQUEST:
                MessageListHandler messageListHandler = new MessageListHandler();
                messageListHandler.setRequest(request);
                return messageListHandler.handleRequest();
            case POST_MESSAGE_REQUEST:
                PostMessageHandler postMessageHandler = new PostMessageHandler();
                postMessageHandler.setRequest(request);
                return postMessageHandler.handleRequest();
            default:
                return null;
        }
    }

    protected Response checkAuthorization() {
        HeaderValues values = request.getHeaders().get("Authorization");
        if (values == null) {
            Unauthorized response = new Unauthorized();
            return response;
        }
        String[] val = values.getFirst().split(" ");
        if (!val[0].equals("Token")) {
            BadRequest response = new BadRequest();
            return response;
        }
        if (users.getUserByToken(val[1]) == null) {
            Forbidden response = new Forbidden();
            return response;
        }
        token = val[1];
        return null;
    }

    protected Response checkMethod(String method) {
        if (!request.getMethod().equals(method)) {
            List<String> methods = Arrays.asList(method);
            MethodNotAllowed response = new MethodNotAllowed(methods);
            return response;
        }
        return null;
    }

    protected Response checkQueryParamsEmpty() {
        if (!request.getQueryParams().isEmpty()) {
            BadRequest response = new BadRequest();
            return response;
        }
        return null;
    }

    protected Response checkRequestContentType(String type) {
        HeaderValues values = request.getHeaders().get("Content-Type");
        String[] contentType = values.getFirst().toString().split("; ");
        if (!contentType[0].equals(type)) {
            BadRequest response = new BadRequest();
            return response;
        }
        return null;
    }
}
