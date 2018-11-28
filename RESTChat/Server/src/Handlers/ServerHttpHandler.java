package Handlers;

import Requests.Request;
import Responses.Response;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;

public class ServerHttpHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        RequestHandler requestHandler = new RequestHandler(new Request(exchange));
        Response response = requestHandler.handleRequest();
        sendResponse(response, exchange);
    }

    private void sendResponse(Response response, HttpServerExchange exchange) {
        exchange.setStatusCode(response.getHttpCode().getValue());
        if (response.getHeaders()!= null) {
            for (HeaderValues values : response.getHeaders())
                exchange.getResponseHeaders().add(values.getHeaderName(), values.toString());
            exchange.getResponseSender().send(response.getContent());
        }
    }
}
