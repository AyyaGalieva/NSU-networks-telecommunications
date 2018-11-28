package Handlers;

import Responses.HttpCode;
import Responses.Response;
import Responses.errors.BadRequest;
import Server.User.UserInfo;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;
import java.time.LocalTime;

import static Server.Main.users;

public class UserInfoHandler extends RequestHandler {
    @Override
    public Response handleRequest() {
        Response resp = checkMethod("GET");
        if (resp != null)
            return resp;

        resp = checkQueryParamsEmpty();
        if (resp != null)
            return resp;

        resp = checkAuthorization();
        if (resp != null)
            return resp;

        String[] val = request.getHeaders().get("Authorization").getFirst().split(" ");
        users.getUserByToken(val[1]).setLastRequestTime(LocalTime.now());

        String[] path = request.getPath().split("/");
        int id;
        try {
            id = Integer.parseInt(path[path.length - 1]);
        }catch (NumberFormatException e) {
            BadRequest response = new BadRequest();
            return response;
        }

        UserInfo userInfo = users.getUserById(id);
        if (userInfo == null) {
            BadRequest response = new BadRequest();
            return response;
        }

        Response response = new Response();
        response.setHttpCode(HttpCode.OK);
        HeaderMap headers = new HeaderMap();
        headers.add(new HttpString("Content-Type"), "application/json");
        response.setHeaders(headers);
        JSONObject json = new JSONObject();
        json.put("id", userInfo.getId());
        json.put("username", userInfo.getUsername());
        response.setContent(ByteBuffer.wrap(json.toString().getBytes()));
        return response;
    }
}
