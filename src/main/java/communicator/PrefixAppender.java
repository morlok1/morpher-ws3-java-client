package communicator;

import com.fasterxml.jackson.core.type.TypeReference;
import exceptions.AccessDeniedException;
import exceptions.ArgumentEmptyException;
import exceptions.InvalidFlagsException;

import java.io.IOException;
import java.util.Map;

public class PrefixAppender implements PathCommunicator {
    private final String prefix;
    private final PathCommunicator communicator;

    public PrefixAppender(PathCommunicator communicator, String prefix) {
        this.communicator = communicator;
        this.prefix = prefix;
    }

    public <T> T sendRequest(String operation, Map<String, String> params, String httpMethod, TypeReference<T> responseType) throws IOException, AccessDeniedException {
        return communicator.sendRequest(Path.combine(prefix, operation), params, httpMethod, responseType);
    }
}
