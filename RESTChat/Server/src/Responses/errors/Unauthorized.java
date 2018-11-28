package Responses.errors;

import Responses.HttpCode;
import Responses.Response;

public class Unauthorized extends Response {
    public Unauthorized() {
        setHttpCode(HttpCode.UNAUTHORIZED);
    }
}
