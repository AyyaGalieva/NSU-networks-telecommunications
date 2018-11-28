package Responses;

import io.undertow.util.HeaderMap;

import java.nio.ByteBuffer;

public class Response {
    private HttpCode httpCode;
    private HeaderMap headers;
    private ByteBuffer content = ByteBuffer.wrap("".getBytes());

    public Response(){}

    public HttpCode getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(HttpCode httpCode) {
        this.httpCode = httpCode;
    }

    public HeaderMap getHeaders() {
        return headers;
    }

    public void setHeaders(HeaderMap headers) {
        this.headers = headers;
    }

    public void setContent(ByteBuffer content) {
        this.content = content;
    }

    public ByteBuffer getContent() {
        return content;
    }
}
