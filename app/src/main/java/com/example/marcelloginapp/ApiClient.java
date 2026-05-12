package com.example.marcelloginapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {

    public static String makePostRequest(String urlString, HashMap<String, String> params) {
        String result = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            
            writer.write(postData.toString());
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            InputStream is;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }

            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                result = response.toString();
                reader.close();
                is.close();
            }
            conn.disconnect();

        } catch (Exception e) {
            result = "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
        }
        return result;
    }
}