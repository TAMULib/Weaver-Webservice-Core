package edu.tamu.weaver.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;

/**
 * Http Utility
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public class HttpUtility {

    @Value("${http.timeout:60000}")
    private static int DEFAULT_TIMEOUT;

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

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);

        conn.setRequestMethod(method);

        conn.setDoOutput(true);

        if (message.isPresent()) {

            conn.setDoInput(true);

            if (contentType.isPresent()) {
                conn.setRequestProperty("Content-type", contentType.get());
            }

            PrintWriter pw = new PrintWriter(conn.getOutputStream());
            pw.write(message.get());
            pw.close();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String inputLine;

        StringBuffer strBufRes = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            strBufRes.append(inputLine);
        }

        in.close();

        return strBufRes.toString();
    }

}
