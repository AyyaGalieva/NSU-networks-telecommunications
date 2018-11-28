package Handlers;

import Responses.HttpCode;
import Responses.Response;
import Server.User.UserInfo;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.util.HashMap;

import static Server.Main.users;

public class UserListHandler extends RequestHandler {
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

        Response response = new Response();
        response.setHttpCode(HttpCode.OK);
        HeaderMap headers = new HeaderMap();
        headers.add(new HttpString("Content-Type"), "application/json");
        response.setHeaders(headers);
        JSONObject json = new JSONObject();
        JSONArray userArray = new JSONArray();
        for (HashMap.Entry<String, UserInfo> entry : users.getNames().entrySet()) {
            JSONObject userJson = new JSONObject();
            userJson.put("id", entry.getValue().getId());
            userJson.put("username", entry.getValue().getUsername());
            userJson.put("token", entry.getValue().getToken());
            userArray.add(userJson);
        }
        json.put("users", userArray);
        response.setContent(ByteBuffer.wrap(json.toString().getBytes()));
        return response;
    }
}
