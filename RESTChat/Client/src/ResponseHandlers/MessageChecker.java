package ResponseHandlers;

import Query.QueryType;
import RequestBuilders.MessageListRequestBuilder;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.TimerTask;

import static Client.Main.client;


public class MessageChecker extends TimerTask {
    private int offset;
    private int count;

    public MessageChecker() {
        this.offset = 0;
        this.count = 100;
    }

    @Override
    public void run() {
        MessageListRequestBuilder messageListRequestBuilder = new MessageListRequestBuilder(offset, count);
        Request getMessageListRequest = messageListRequestBuilder.getRequest();
        Call call = client.newCall(getMessageListRequest);
        try {
            Response response = call.execute();
            MessageListResponseHandler messageListResponseHandler = new MessageListResponseHandler(response);
            messageListResponseHandler.handleResponse(this);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
