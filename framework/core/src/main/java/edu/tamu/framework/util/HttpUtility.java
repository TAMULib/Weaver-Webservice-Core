/* 
 * HttpUtility.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Service;

@Service
public class HttpUtility {

	public String makeHttpRequest(String urlString, String method) throws IOException {
		
		URL url = new URL(urlString);
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
 
		con.setRequestMethod(method);
 
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		
		String inputLine;
		
		StringBuffer strBufRes = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			strBufRes.append(inputLine);
		}
		
		in.close();
		
		return strBufRes.toString();
	}
	
}
