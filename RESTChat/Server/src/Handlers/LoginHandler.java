package Handlers;

import Responses.HttpCode;
import Responses.Response;
import Responses.errors.BadRequest;
import Responses.errors.InternalServerError;
import Server.User.UserInfo;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import static Server.Main.users;

public class LoginHandler extends RequestHandler {
    @Override
    public Response handleRequest() {
        Response resp = checkMethod("POST");
        if (resp != null)
            return resp;

        resp = checkQueryParamsEmpty();
        if (resp != null)
            return resp;

        resp = checkRequestContentType("application/json");
        if (resp != null)
            return resp;

        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(new String(request.getContent(), StandardCharsets.UTF_8));
            if ((json.get("username") == null)||(json.size() > 1)) {
                BadRequest response = new BadRequest();
                return response;
            }
            String username = json.get("username").toString();
            UserInfo user = users.addUser(username);
            if (user == null) {
                InternalServerError response = new InternalServerError();
                return response;
            }

            Response response = new Response();
            response.setHttpCode(HttpCode.OK);
            HeaderMap headers = new HeaderMap();
            headers.add(new HttpString("Content-Type"), "application/json");
            response.setHeaders(headers);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("id", users.getUserByName(username).getId());
            jsonResponse.put("username", username);
            jsonResponse.put("token", users.getUserByName(username).getToken());
            response.setContent(ByteBuffer.wrap(jsonResponse.toString().getBytes()));
            return response;

        } catch (ParseException e) {
            BadRequest response = new BadRequest();
            return response;
        }
    }
}
