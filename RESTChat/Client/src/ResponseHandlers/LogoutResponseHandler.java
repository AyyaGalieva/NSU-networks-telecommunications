package ResponseHandlers;

import okhttp3.Response;

import static Client.Main.timer;
import static Client.Main.token;

public class LogoutResponseHandler extends ResponseHandler {
    public LogoutResponseHandler(Response response) {
        this.response = response;
    }

    @Override
    public void handleResponse() {
        if (response.code() == 403) {
            System.out.println("you are not authorized");
            return;
        }

        System.out.println("bye!");
        token = null;

        timer.cancel();
        timer.purge();
    }
}
