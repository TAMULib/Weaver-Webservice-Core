package edu.tamu.weaver.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

/**
 * Http Utility
 *
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 */
public class HttpUtility {

    private static int DEFAULT_TIMEOUT = 60000;

    public static String makeHttpRequest(String urlString, String method) throws IOException {
        return makeHttpRequest(urlString, method, Optional.empty(), Optional.empty(), DEFAULT_TIMEOUT);
    }

    public static String makeHttpRequest(String urlString, String method, String message) throws IOException {
        return makeHttpRequest(urlString, method, Optional.of(message), Optional.empty(), DEFAULT_TIMEOUT);
    }

    public static String makeHttpRequest(String urlString, String method, String message, String contentType) throws IOException {
        return makeHttpRequest(urlString, method, Optional.of(message), Optional.of(contentType), DEFAULT_TIMEOUT);
    }

    public static String makeHttpRequest(String urlString, String method, String message, int timeout) throws IOException {
        return makeHttpRequest(urlString, method, Optional.of(message), Optional.empty(), timeout);
    }

    public static String makeHttpRequest(String urlString, String method, String message, String contentType, int timeout) throws IOException {
        return makeHttpRequest(urlString, method, Optional.of(message), Optional.of(contentType), timeout);
    }

    public static String makeHttpRequest(String urlString, String method, Optional<String> message, Optional<String> contentType, int timeout) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);

        connection.setRequestMethod(method);

        connection.setDoOutput(true);

        connection.setRequestProperty("Accept", "*/*");

        if (message.isPresent()) {

            connection.setDoInput(true);

            if (contentType.isPresent()) {
                connection.setRequestProperty("Content-type", contentType.get());
            }

            PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
            printWriter.write(message.get());
            printWriter.close();
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String inputLine;

        StringBuffer strBufRes = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {
            strBufRes.append(inputLine);
        }

        bufferedReader.close();

        connection.disconnect();

        return strBufRes.toString();
    }

}
