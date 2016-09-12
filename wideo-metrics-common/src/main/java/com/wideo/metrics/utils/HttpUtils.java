package com.wideo.metrics.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.wideo.metrics.utils.LogEvent;



public class HttpUtils {

    private static final Logger LOGGER = Logger.getLogger(HttpUtils.class);

    /**
     * Get
     * 
     * @param url
     * @return
     */
    public static JSONObject getJSONObject(String url) {

        JSONObject json = new JSONObject();

        try {
            // A Simple JSON Response Read
            String result = urlGet(url);
            // StringBuffer result = getUrlContent(url);
            if (result != null) {
                json = new JSONObject(result);
            }

        }
        catch (Exception e) {
            LOGGER.error(LogEvent.args("No se pudo obtener el json de: ", url));
        }

        return json;
    }

    public static JSONObject getJSONObject(String url, JSONObject postBody,
            String contentType) {
        return getJSONObject(url, postBody.toString(), contentType);
    }

    public static JSONObject getJSONObject(String url, JSONObject postBody) {
        return getJSONObject(url, postBody, "application/json");
    }

    public static JSONObject getJSONObject(String url, String postBody) {
        return getJSONObject(url, postBody, "application/json");
    }

    public static Map<String, String> getVarsMap(String url, String postBody,
            String contentType) {
        try {
            String result = urlPost(url, postBody, contentType)// )
                    .toString();

            return splitQuery(result);
        }
        catch (Exception e) {
            LOGGER.error(LogEvent.args("Ocurrio un problema", e));
        }
        return null;
    }

    private static Map<String, String> splitQuery(String query)
            throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static JSONObject getJSONObject(String url, String postBody,
            String contentType) {

        JSONObject json = new JSONObject();

        try {
            // "application/x-www-form-urlencoded"
            String result = urlPost(url, postBody, contentType)// )
                    .toString();
            // String result = postJson(url, postBody).toString();
            json = new JSONObject(result);
        }
        catch (Exception e) {
            LOGGER.error(LogEvent.args("Ocurrio un problema", e));
        }
        return json;
    }

    /**
     * Get
     * 
     * @param url
     * @return
     */
    public static JSONArray getJSONArray(String url) {

        JSONArray json = null;

        try {
            String content = urlGet(url);

            if (content != null)
                json = new JSONArray(content);

        }
        catch (Exception e) {
            LOGGER.error(LogEvent.args("Ocurrio un problema", e));
        }

        return json;
    }

    public static String urlGet(String url) {
        // <meta charset="utf-8">
        try {
            URL server = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) server
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("Accept", "application/json");
            connection.addRequestProperty("Accept-Charset", "ISO-8859-1,UTF-8");
            connection.setAllowUserInteraction(false);

            int code = connection.getResponseCode();
            StringBuilder content = new StringBuilder();

            InputStream input;
            if (code >= 400) {
                input = connection.getErrorStream();
            }
            else {
                input = connection.getInputStream();
            }

            BufferedReader docHtml = new BufferedReader(new InputStreamReader(
                    input, "UTF-8"));

            String line;
            while ((line = docHtml.readLine()) != null) {
                content.append(line);
            }
            docHtml.close();
            connection.disconnect();
            return content.toString();

        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String urlPost(String url, String body, String contentType) {

        try {
            URL server = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) server
                    .openConnection();
            connection.setRequestMethod("POST");
//            connection.addRequestProperty("Accept", "application/json");
//            connection.addRequestProperty("Content-Type", contentType);
//            connection.setRequestProperty("Content-Length",
//                    "" + Integer.toString(body.getBytes().length));
//            connection.setRequestProperty("Content-Language", "en-US");
//            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();
            int code = connection.getResponseCode();
            StringBuilder content = new StringBuilder();

            InputStream input;
            if (code >= 400) {
                input = connection.getErrorStream();
            }
            else {
                input = connection.getInputStream();
            }
            BufferedReader docHtml = new BufferedReader(new InputStreamReader(
                    input));
            String line;
            while ((line = docHtml.readLine()) != null) {
                content.append(line);
            }
            docHtml.close();
            connection.disconnect();
            return content.toString();
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }
    
    public static String urlPost(String url, Map<String, String> headerProperties, String body, String contentType) {

        try {
            URL server = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) server
                    .openConnection();
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Accept", "application/json");
            connection.addRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Content-Length",
                    "" + Integer.toString(body.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            for (String key : headerProperties.keySet()) {
                connection.addRequestProperty(key, headerProperties.get(key));
            }
            
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();
            int code = connection.getResponseCode();
            StringBuilder content = new StringBuilder();

            InputStream input;
            if (code >= 400) {
                input = connection.getErrorStream();
            }
            else {
                input = connection.getInputStream();
            }
            BufferedReader docHtml = new BufferedReader(new InputStreamReader(
                    input));
            String line;
            while ((line = docHtml.readLine()) != null) {
                content.append(line);
            }
            docHtml.close();
            connection.disconnect();
            return content.toString();
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

}
