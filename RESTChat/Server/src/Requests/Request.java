package Requests;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.Map;

import static java.lang.Character.isLetter;

public class Request {
    private RequestType type;
    private String method;
    private String path;
    private HeaderMap headers;
    private byte[] content;
    private Map<String,Deque<String>> queryParams;

    private String token;
    private long contentLength;

    public Request(HttpServerExchange exchange) {
        method = exchange.getRequestMethod().toString();
        path = exchange.getRequestPath();
        headers = exchange.getRequestHeaders();
        token = exchange.getRequestHeaders().get(Headers.AUTHORIZATION, 0);
        contentLength = exchange.getRequestContentLength();
        if (contentLength<0) contentLength = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate((int)contentLength);
        try {
            exchange.getRequestChannel().read(byteBuffer);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        content = byteBuffer.array();
        queryParams = exchange.getQueryParameters();

        String requestKey = getRequestKey();
        switch (requestKey) {
            case "/login":
                type = RequestType.LOGIN_REQUEST;
                break;
            case "/logout":
                type = RequestType.LOGOUT_REQUEST;
                break;
            case "/users":
                type = RequestType.GET_USERLIST_REQUEST;
                break;
            case "/users/":
                type = RequestType.GET_USER_INFO;
                break;
            case "/messages":
                if (queryParams.isEmpty()) {
                    type = RequestType.POST_MESSAGE_REQUEST;
                }
                else  {
                    type = RequestType.GET_MESSAGESLIST_REQUEST;
                }
                break;
        }
    }

    String getRequestKey() {
        String res = "";
        for (char c : path.toCharArray()) {
            if (isLetter(c)||c == '/')
                res += c;
            else break;
        }
        return res;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HeaderMap getHeaders() {
        return headers;
    }

    public void setHeaders(HeaderMap headers) {
        this.headers = headers;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getToken() {
        return token;
    }

    public long getContentLength() {
        return contentLength;
    }

    public Map<String,Deque<String>> getQueryParams() {
        return queryParams;
    }

    public RequestType getType() {
        return type;
    }
}
