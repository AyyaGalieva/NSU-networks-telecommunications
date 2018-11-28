package Responses.errors;

import Responses.HttpCode;
import Responses.Response;

public class BadRequest extends Response {
    public BadRequest() {
        setHttpCode(HttpCode.BAD_REQUEST);
    }
}
