package Responses.errors;

import Responses.HttpCode;
import Responses.Response;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;

import java.util.List;

public class MethodNotAllowed extends Response {
    public MethodNotAllowed(List<String> allowedMethods) {
        setHttpCode(HttpCode.METHOD_NOT_ALLOWED);
        HeaderMap headers = new HeaderMap();
        headers.add(new HttpString("Allow"), String.join(", ", allowedMethods));
        setHeaders(headers);
    }
}
