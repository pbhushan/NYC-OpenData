package bhushan.doc.pusp.nycaccidentmap.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PBhushan on 2/28/2015.
 */
public class RestClient {

    public static final String REQUEST_PROP_KEY_USER_AGENT = "http.useragent";
    public static final String REQUEST_PROP_KEY_CACHE_CONTROL = "Cache-Control";
    private HashMap<String, String> params;
    private HashMap<String, String> headers;

    private String url;
    private int responseCode;
    private String extra_url;
    private String message;
    private String response;
    private InputStream responseStream;
    private String postData;

    /**
     * The timeout in milliseconds until a connection is established.
     */
    private int timeoutConnection;

    /**
     * The socket timeout in milliseconds which is the time for waiting for a
     * reply after connection.
     */
    private int timeoutSocket;

    public RestClient(String url) {
        this.url = url;
        params = new HashMap<String, String>();
        headers = new HashMap<String, String>();
        timeoutConnection = 5000;
        timeoutSocket = 5000;
        addHeader(REQUEST_PROP_KEY_USER_AGENT, "Applico Interview Excercise");
    }

    private static String convertStreamToString(InputStream is) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    //public void addParam(String name, String value) {
    //    params.put(name, value);
    // }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setPostData(String data) {
        this.postData = data;
    }

    public void execute(RequestMethod method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(this.getFullUrl()).openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setReadTimeout(timeoutSocket);
        connection.setConnectTimeout(timeoutConnection);
        // add headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }
        switch (method) {
            case POST:
                connection.setDoOutput(true);
                connection.setChunkedStreamingMode(0);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());

                out.writeBytes(postData);
                out.flush();
                out.close();
                break;

        }
        executeRequest(connection);
    }

    public String getErrorMessage() {
        return message;
    }

    public String getFullUrl() throws UnsupportedEncodingException {
        return url + getUrlParametersString();
    }

    public String getExtraUrl() {
        return extra_url;
    }

    public void setExtraUrl(String extra_url) {
        this.extra_url = extra_url;
    }

    public String getResponse() {
        return response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public InputStream getResponseStream() {
        return responseStream;
    }

    /**
     * Gets the timeout in milliseconds until a connection is established.
     *
     * @return the timeout in milliseconds
     */
    public int getTimeoutConnection() {
        return timeoutConnection;
    }

    /**
     * Sets the timeout in milliseconds until a connection is established.
     *
     * @param timeoutConnection the timeout to set in milliseconds
     */
    public void setTimeoutConnection(int timeoutConnection) {
        this.timeoutConnection = timeoutConnection;
    }

    /**
     * Gets the socket timeout in milliseconds which is the time for waiting for
     * a reply after connection.
     *
     * @return the timeout in milliseconds
     */
    public int getTimeoutSocket() {
        return timeoutSocket;
    }

    /**
     * Sets the socket timeout in milliseconds which is the time for waiting for
     * a reply after connection
     *
     * @param timeoutSocket the timeout to set in milliseconds
     */
    public void setTimeoutSocket(int timeoutSocket) {
        this.timeoutSocket = timeoutSocket;
    }

    public void setCacheMaxAge(int maxAgeInMinutes) {
        //addHeader(REQUEST_PROP_KEY_CACHE_CONTROL, "max-age=" + (maxAgeInMinutes * 60));
    }

    public void setCacheMaxStale(int maxStaleInMinutes) {
        addHeader(REQUEST_PROP_KEY_CACHE_CONTROL, "max-stale=" + (maxStaleInMinutes * 60));
    }

    public boolean isRequestSuccessful() {
        return this.getResponseCode() == HttpURLConnection.HTTP_OK ||
                this.getResponseCode() == HttpURLConnection.HTTP_CREATED;
    }

    private void executeRequest(HttpURLConnection connectionRequest)
            throws IOException {

        try {

            responseStream = connectionRequest.getInputStream();

            responseCode = connectionRequest.getResponseCode();
            message = connectionRequest.getResponseMessage();

            response = convertStreamToString(responseStream);

            // Closing the input stream will trigger connection release
            responseStream.close();

        } finally {
            connectionRequest.disconnect();
        }
    }

    private String getUrlParametersString() throws UnsupportedEncodingException {
        // add parameters
        String combinedParams = "";
        if (!extra_url.isEmpty()) {
            combinedParams += "";
            //   String paramString = URLEncoder.encode(extra_url, "UTF-8");
            combinedParams += extra_url;

        }
        return combinedParams;
    }


    public enum RequestMethod {
        GET, POST
    }

}