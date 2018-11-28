package Handlers;

import Responses.HttpCode;
import Responses.Response;
import Responses.errors.BadRequest;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

import static Server.Main.messages;
import static Server.Main.users;

public class PostMessageHandler extends RequestHandler {
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

        resp = checkRequestContentType("application/json");
        if (resp != null)
            return resp;

        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(new String(request.getContent(), StandardCharsets.UTF_8));
            if ((json.get("message") == null)||(json.size() > 1)) {
                BadRequest response = new BadRequest();
                return response;
            }
            String msg = json.get("message").toString();
            int userId = users.getUserByToken(token).getId();
            int messageId = messages.postMessage(userId, msg);

            String[] val = request.getHeaders().get("Authorization").getFirst().split(" ");
            users.getUserByToken(val[1]).setLastRequestTime(LocalTime.now());

            Response response = new Response();
            response.setHttpCode(HttpCode.OK);
            HeaderMap headers = new HeaderMap();
            headers.add(new HttpString("Content-Type"), "application/json");
            response.setHeaders(headers);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("id", messageId);
            jsonResponse.put("message", msg);
            response.setContent(ByteBuffer.wrap(jsonResponse.toString().getBytes()));
            return response;

        } catch (ParseException e) {
            BadRequest response = new BadRequest();
            return response;
        }
    }
}
