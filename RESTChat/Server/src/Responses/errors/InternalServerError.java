package Responses.errors;

import Responses.HttpCode;
import Responses.Response;

public class InternalServerError extends Response{
    public InternalServerError() {
        setHttpCode(HttpCode.INTERNAL_SERVER_ERROR);
    }
}
