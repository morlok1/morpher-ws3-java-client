package ru.morpher.ws3.communicator;

import ru.morpher.ws3.AccessDeniedException;

import java.io.IOException;
import java.util.Map;

public class UrlAuthCommunicator implements Communicator {

    private final String token;
    private final Communicator communicator;

    public UrlAuthCommunicator(String token, Communicator communicator) {
        this.token = token;
        this.communicator = communicator;
    }

    public String sendRequest(String url, Map<String, String> params, String method) throws IOException, AccessDeniedException {
        //Keep appender logic since url may already contain some url params in future for some reason
        String appenderChar = url.contains("?") ? "&" : "?";
        url = url + appenderChar + "token=" + token;

        return communicator.sendRequest(url, params, method);
    }
}
