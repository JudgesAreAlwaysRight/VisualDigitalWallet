package com.example.visualwallet.net;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.visualwallet.common.Constant;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NetUtil {

    private static String urlBase;
    private static HashMap<Character, String> hex2bin;

    static {
        urlBase = Constant.protocol
                + "://"
                + Constant.domain
                + ":"
                + Constant.port
                + Constant.projectRoot;
        hex2bin = new HashMap<>();
        hex2bin.put('0', "0000");
        hex2bin.put('1', "0001");
        hex2bin.put('2', "0010");
        hex2bin.put('3', "0011");
        hex2bin.put('4', "0100");
        hex2bin.put('5', "0101");
        hex2bin.put('6', "0110");
        hex2bin.put('7', "0111");
        hex2bin.put('8', "1000");
        hex2bin.put('9', "1001");
        hex2bin.put('a', "1010");
        hex2bin.put('b', "1011");
        hex2bin.put('c', "1100");
        hex2bin.put('d', "1101");
        hex2bin.put('e', "1110");
        hex2bin.put('f', "1111");
    }

    public static Map<String, Object> Get(String subUrlStr, Map<String, Object> args) {

        HttpURLConnection connection = null;
        String response = null;

        StringBuilder urlStr = new StringBuilder(urlBase + subUrlStr);
        boolean firstArg = true;
        if (args != null) {
            for (Map.Entry<String, Object> it : args.entrySet()) {
                urlStr.append(firstArg ? "?" : "&");
                if (firstArg) {
                    firstArg = false;
                }
                urlStr.append(it.getKey());
                urlStr.append("=");
                urlStr.append(args.get(it.getValue().toString()));
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
        return JSONObject.parseObject(response);
    }

    public static Map Post(String subUrlStr, Map<String, Object> args) {

        HttpURLConnection connection = null;
        String response = null;
        int code;

        String jsonString = JSON.toJSONString(args);
        byte[] argsByte = jsonString.getBytes();

        Log.i("post url", urlBase + subUrlStr + "/");
        Log.i("post body", jsonString.toString());

        try {
            URL url = new URL(urlBase + subUrlStr + "/");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(Constant.connectTimeout);
            connection.setReadTimeout(Constant.readTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(argsByte, 0, argsByte.length);

            code = connection.getResponseCode();
            InputStream in;
            if (code == 200) {
                in = connection.getInputStream(); // 得到网络返回的正确输入流
            } else {
                in = connection.getErrorStream(); // 得到网络返回的错误输入流
            }
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            response = reader.readLine();
            Log.i("response", response);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        if (code == 200) {
            Map resMap = (Map) JSONObject.parse(response);
            resMap.put("code", "200");
            return resMap;
        } else {
            Map<String, String> errorMap = new HashMap<String, String>();
            errorMap.put("code", String.valueOf(code));
            return errorMap;
        }
    }

    public static String key2bin(String keyStr) {
        Log.i("NetUtil key2bin", String.valueOf(keyStr.length()));
        if (keyStr.length() == 64 || // 标准长度
                (keyStr.length() == 66 && keyStr.charAt(0) == '0' && // 带有0x或0X前缀
                        (keyStr.charAt(1) == 'x' || keyStr.charAt(1) == 'X'))) {
            if (keyStr.length() == 66) {
                keyStr = keyStr.substring(2);
            }

            StringBuilder binKey = new StringBuilder(256);
            for (int i = 0; i < keyStr.length(); i++) {
                String t = hex2bin.get(keyStr.charAt(i));
                if (t != null) {
                    binKey.append(t);
                } else return null;
            }
            return binKey.toString();
        }
        return null;
    }

    public static String key2hex(String keyStr) {
        if (keyStr.length() == 256) {
            StringBuilder keyBuilder = new StringBuilder(64);
            for (int i = 0; i < 64; i++) {
                int t = 0, base = 8;
                for (int j = 0; j < 4; j++, base /= 2) {
                    char c = keyStr.charAt(i * 4 + j);
                    if (c == '1')
                        t += base;
                    else if (c != '0')
                        return null;
                }
                keyBuilder.append(Integer.toHexString(t));
            }
            return keyBuilder.toString();
        }
        return null;
    }

    public static int[][][] arrayJson2java(JSONArray jsonArray) {
        if (jsonArray == null)
            return null;
        int[][][] arr = new int[jsonArray.size()][105][105];
        for (int k = 0; k < jsonArray.size(); k++) {
            JSONArray mat = (JSONArray) jsonArray.get(k);
            for (int i = 0; i < mat.size(); i++) {
                JSONArray line = (JSONArray) mat.get(i);
                if (line == null || line.size() != 105) {
                    if(line != null)
                        Log.i("arrayJson2java", String.valueOf(line.size()));
                    else
                        Log.i("arrayJson2java", String.format("got null jsonArray at (%d,%d)", k, i));
                    return null;
                }
                for (int j = 0; j < line.size(); j++) {
                    arr[k][i][j] = (Integer) line.get(j);
                }
            }
        }
        return arr;
    }
}
