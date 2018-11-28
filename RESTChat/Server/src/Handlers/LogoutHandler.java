package Handlers;

import Responses.HttpCode;
import Responses.Response;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;

import static Server.Main.messages;
import static Server.Main.users;

public class LogoutHandler extends RequestHandler {
    @Override
    public Response handleRequest() {
        Response resp = checkMethod("POST");
        if (resp != null)
            return resp;

        resp = checkQueryParamsEmpty();
        if (resp != null)
            return resp;

        resp = checkAuthorization();
        if (resp != null)
            return resp;

        messages.postMessage(users.getUserByToken(token).getId(), "*logged out*");
        users.removeUser(users.getUserByToken(token).getUsername());

        Response response = new Response();
        response.setHttpCode(HttpCode.OK);
        HeaderMap headers = new HeaderMap();
        headers.add(new HttpString("Content-Type"), "application/json");
        response.setHeaders(headers);
        JSONObject json = new JSONObject();
        json.put("message", "bye!");
        response.setContent(ByteBuffer.wrap(json.toString().getBytes()));
        return response;
    }
}
