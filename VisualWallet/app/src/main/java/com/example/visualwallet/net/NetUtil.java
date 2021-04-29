package com.example.visualwallet.net;

import android.util.Log;

import com.example.visualwallet.common.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class NetUtil {

    private static String urlBase;

    static {
        urlBase = Constant.protocol + "://" + Constant.domain;
        if (!Constant.domain.equals("80"))
            urlBase += ":" + Constant.port;
    }

    public static String Get(String subUrlStr, Map<String, String> args) {

        HttpURLConnection connection = null;
        String response = null;

        StringBuilder urlStr = new StringBuilder(urlBase + subUrlStr);
        boolean firstArg = true;
        if (args != null) {
            for (Map.Entry<String, String> it : args.entrySet()) {
                urlStr.append(firstArg ? "?" : "&");
                if (firstArg) {
                    firstArg = false;
                }
                urlStr.append(it.getKey());
                urlStr.append("=");
                urlStr.append(args.get(it.getValue()));
            }
        }

        try {
            URL url = new URL(urlStr.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(Constant.connectTimeout);
            connection.setReadTimeout(Constant.readTimeout);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            InputStream in = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            response = reader.readLine();

        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

    public static String Post(String subUrlStr, Map<String, String> args) {

        HttpURLConnection connection = null;
        String response = null;

        StringBuilder argsBuf = new StringBuilder();

        boolean firstArg = true;
        if (args != null) {
            for (Map.Entry<String, String> it : args.entrySet()) {
                argsBuf.append(firstArg ? "" : "&");
                if (firstArg) {
                    firstArg = false;
                }
                argsBuf.append(it.getKey());
                argsBuf.append("=");
                argsBuf.append(args.get(it.getValue()));
            }
        }
        byte[] argsByte = argsBuf.toString().getBytes();

        try {
            URL url = new URL(urlBase + subUrlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(Constant.connectTimeout);
            connection.setReadTimeout(Constant.readTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(argsByte.length));

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(argsByte, 0, argsByte.length);

            InputStream in = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            response = reader.readLine();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }
}
