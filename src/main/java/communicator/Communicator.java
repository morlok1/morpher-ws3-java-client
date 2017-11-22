package communicator;

import exceptions.MorpherException;

import java.io.IOException;
import java.util.Map;

public interface Communicator {
    String METHOD_GET = "GET";
    String METHOD_DELETE = "DELETE";
    String METHOD_POST = "POST";

    String sendRequest(String url, Map<String, String> params, String method) throws IOException, MorpherException;
}
