package Handlers;

import Responses.HttpCode;
import Responses.Response;
import Responses.errors.BadRequest;
import Server.Message.MessageInfo;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.util.ArrayList;

import static Server.Main.messages;
import static Server.Main.users;

public class MessageListHandler extends RequestHandler {
    @Override
    public Response handleRequest() {
        Response resp = checkMethod("GET");
        if (resp != null)
            return resp;
        int offset, count;

        try {
            offset = Integer.parseInt(request.getQueryParams().get("offset").getFirst());
            count = Integer.parseInt(request.getQueryParams().get("count").getFirst());
        } catch (NumberFormatException e) {
            BadRequest response = new BadRequest();
            return response;
        }
        if ((offset < 0)||(count < 0)||(offset > 100)||(count > 100)){
            BadRequest response = new BadRequest();
            return response;
        }

        resp = checkAuthorization();
        if (resp != null)
            return resp;

        Response response = new Response();
        response.setHttpCode(HttpCode.OK);
        HeaderMap headers = new HeaderMap();
        headers.add(new HttpString("Content-Type"), "application/json");
        response.setHeaders(headers);
        JSONObject json = new JSONObject();

        ArrayList<MessageInfo> messageInfos = messages.getMessages(offset, count);
        if (messageInfos == null) {
            response.setContent(ByteBuffer.wrap("".toString().getBytes()));
            return response;
        }

        String[] val = request.getHeaders().get("Authorization").getFirst().split(" ");
        String token = val[1];

        users.getUserByToken(token).setLastRequestTime(LocalTime.now());

        JSONArray messageArray = new JSONArray();
        for (MessageInfo messageInfo : messageInfos) {
            JSONObject messageJson = new JSONObject();
            messageJson.put("id", messageInfo.getId());
            messageJson.put("message", messageInfo.getMessage());
            messageJson.put("author", messageInfo.getAuthor());
            messageArray.add(messageJson);
        }
        json.put("messages", messageArray);

        response.setContent(ByteBuffer.wrap(json.toString().getBytes()));
        return response;
    }
}
