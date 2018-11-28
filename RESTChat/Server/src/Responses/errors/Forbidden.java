package Responses.errors;

import Responses.HttpCode;
import Responses.Response;

public class Forbidden extends Response{
    public Forbidden() {
        setHttpCode(HttpCode.FORBIDDEN);
    }
}
