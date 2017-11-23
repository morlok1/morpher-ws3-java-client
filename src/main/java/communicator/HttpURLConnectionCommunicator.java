package communicator;

import communicator.connection.ConnectionHandler;
import exceptions.MorpherException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;

public class HttpURLConnectionCommunicator implements Communicator {

    private final ConnectionHandler connectionHandler;

    public HttpURLConnectionCommunicator(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public String sendRequest(String url, Map<String, String> params, String method) throws IOException, MorpherException {
        String requestParameters = toConcatenatedRequestParameters(params);

        if (!isPost(method)) {
            String appenderChar = url.contains("?") ? "&" : "?";
            url = url + appenderChar + requestParameters;
        }

        HttpURLConnection con = getHttpConnection(url, method);

        if (isPost(method)) {
            populatePostParams(requestParameters, con);
        }

        con.connect();

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            String responseErrorBody = toResponseBody(con.getErrorStream());
            handleErrors(responseCode, responseErrorBody);
        }

        return toResponseBody(con.getInputStream());
    }

    private static boolean isPost(String method) {
        return method.equalsIgnoreCase(METHOD_POST);
    }

    static String toConcatenatedRequestParameters(Map<String, String> params) throws UnsupportedEncodingException {
        if (params == null || params.size() == 0) {
            return "";
        }

        StringBuilder paramsString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey() == null || entry.getKey().trim().length() == 0 ||
                    entry.getValue() == null || entry.getValue().trim().length() == 0) {
                // ignore empty entries
                continue;
            }

            String appenderChar = paramsString.length() == 0 ? "" : "&";
            paramsString.append(appenderChar);
            paramsString.append(entry.getKey());
            paramsString.append("=");
            paramsString.append(URLEncoder.encode(entry.getValue(), "UTF8"));
        }

        return paramsString.toString();
    }

    private HttpURLConnection getHttpConnection(String urlString, String method) throws IOException {
        HttpURLConnection con = connectionHandler.openConnection(urlString);
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);
        con.setRequestMethod(method);
        con.setInstanceFollowRedirects(false);
        con.setUseCaches(false);
        con.setDoOutput(true);

        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Accept", "application/json");

        return con;
    }

    static void populatePostParams(String urlParameters, HttpURLConnection con) throws IOException {
        byte[] postData = urlParameters.getBytes("UTF8");
        int postDataLength = postData.length;

        con.setRequestProperty("Content-Length", Integer.toString(postDataLength));

        DataOutputStream outputStream = new DataOutputStream(con.getOutputStream());
        outputStream.write(postData);
    }

    private static String toResponseBody(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder responseBuilder = new StringBuilder();

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            responseBuilder.append(inputLine);
        }

        in.close();

        return responseBuilder.toString();
    }


    private void handleErrors(int responseCode, String responseErrorBody) throws MorpherException {

        switch (responseCode) {
            case 402:
                throw new MorpherException("Превышен лимит на количество запросов");
            case 403:
                throw new MorpherException("IP-адрес заблокирован");
            case 495:
                throw new MorpherException("Для склонения числительных используйте метод spell");
            case 496:
                throw new MorpherException("Не найдено русских слов");
            case 400:
                throw new MorpherException("Передана пустая строка");
            case 498:
                throw new MorpherException("Переданный токен не найден");
            case 497:
                throw new MorpherException("Неверный формат токена");
            default:
                throw new RuntimeException("Unexpected error: " + responseErrorBody);
        }
    }
}