package com.visualwallet.net;

import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.visualwallet.common.Constant;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetUtil {

    private static final String urlBase;
    private static final HashMap<Character, String> hex2bin;

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

    public static Map<String, Object> Get(String urlStr, Map<String, Object> args) {

        HttpURLConnection connection = null;
        String response = null;

        StringBuilder urlStrBuilder = new StringBuilder(urlStr);
        boolean firstArg = true;
        if (args != null) {
            for (Map.Entry<String, Object> it : args.entrySet()) {
                urlStrBuilder.append(firstArg ? "?" : "&");
                if (firstArg) {
                    firstArg = false;
                }
                urlStrBuilder.append(it.getKey());
                urlStrBuilder.append("=");
                urlStrBuilder.append(args.get(it.getValue().toString()));
            }
        }

        try {
            URL url = new URL(urlStrBuilder.toString());
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

    public static Map Post(String urlStr, Map<String, Object> args) {
        return Post(urlStr, args, true);
    }


    public static Map Post(String urlStr, Map<String, Object> args, boolean fixDash) {

        HttpURLConnection connection = null;
        String response;
        int code;

        String jsonString = JSON.toJSONString(args);
        byte[] argsByte = jsonString.getBytes();

        Log.i("post url", urlStr + (fixDash ? "/" : ""));
        Log.i("post body", jsonString);

        try {
            URL url = new URL(urlStr + (fixDash ? "/" : ""));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(Constant.connectTimeout);
            connection.setReadTimeout(Constant.readTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            // connection.setRequestProperty("Content-Type", "multipart/form-data");

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(argsByte, 0, argsByte.length);

            code = connection.getResponseCode();
            if (code != 200) {
                Log.w("NetUtil", "post ret code: " + String.valueOf(code));
            }
            InputStream in = connection.getInputStream(); // 得到网络返回的正确输入流
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder resBuffer = new StringBuilder();
            String line;
            while((line=reader.readLine()) != null){
                resBuffer.append(line).append("\n");
            }
            response = resBuffer.toString();
            Log.i("response", response);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        if (code < 300) {
            try {
                Map resMap = (Map) JSONObject.parse(response);
                resMap.put("code", String.valueOf(code));
                return resMap;
            }
            catch (JSONException je) {
                Log.e("NetUtil", "JSONException");
                je.printStackTrace();
                Map<String, String> resMap = new HashMap<>();
                resMap.put("code", String.valueOf(code));
                return resMap;
            }
        } else {
            Map<String, String> errorMap = new HashMap<String, String>();
            errorMap.put("code", String.valueOf(code));
            return errorMap;
        }
    }

    public static Map filePost(String urlStr, Map<String, Object> args, boolean fixDash) {
        // 纯上传型文件传输post（multipart/form-data格式，文件位于表单中，可携带普通文本参数）
        HttpURLConnection connection = null;
        String response;
        int code;
        String boundary = "----WebKitFormBoundary" + java.util.UUID.randomUUID().toString().replace("-", "");

        try {
            URL url = new URL(urlStr + (fixDash ? "/" : ""));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(Constant.connectTimeout);
            connection.setReadTimeout(Constant.readTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
            for (Map.Entry<String, Object> entry : args.entrySet()) {
                if (entry.getValue() instanceof File) {
                    // 文件型数据
                    File file = (File) entry.getValue();
                    String fileName = file.getName();
                    if (!fileName.endsWith(".wav")) {
                        Log.e("filePost", "got unknown file type, name=\"" + fileName + "\"");
                    }
                    String fileType = "audio/wav";
                    String content = "--" + boundary + "\r\n" +
                            "Content-Disposition: form-data; name=\"" + entry.getKey() +
                            "\"; filename=\"" + file.getName() + "\r\n" +
                            "Content-Type: " + fileType + "\r\n\r\n";
                    outStream.write(content.getBytes());

                    InputStream is = new FileInputStream(file);
                    byte[] buffer = new byte[5 * 1024 * 1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    is.close();
                    outStream.write("\r\n".getBytes());
                } else if(entry.getValue() instanceof String || entry.getValue() instanceof Integer) {
                    // 字符串与数值型数据
                    String content = "--" + boundary + "\r\n" +
                            "Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n" +
                            entry.getValue() + "\r\n";
                    outStream.write(content.getBytes());
                } else {
                    Log.e("filePost", "got param which cann`t be managed");
                    return null;
                }
            }
            outStream.write(("--" + boundary + "--\r\n").getBytes());
            outStream.flush();

            code = connection.getResponseCode();
            if (code != 200) {
                Log.w("NetUtil", "post ret code: " + String.valueOf(code));
            }
            InputStream in = connection.getInputStream(); // 得到网络返回的正确输入流
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder resBuffer = new StringBuilder();
            String line;
            while((line=reader.readLine()) != null){
                resBuffer.append(line).append("\n");
            }
            response = resBuffer.toString();
            Log.i("response", response);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        if (code < 300) {
            try {
                Map resMap = (Map) JSONObject.parse(response);
                resMap.put("code", String.valueOf(code));
                return resMap;
            }
            catch (JSONException je) {
                Log.e("NetUtil", "JSONException");
                je.printStackTrace();
                Map<String, String> resMap = new HashMap<>();
                resMap.put("code", String.valueOf(code));
                return resMap;
            }
        } else {
            Map<String, String> errorMap = new HashMap<String, String>();
            errorMap.put("code", String.valueOf(code));
            return errorMap;
        }
    }

    public static Map filePost(String urlStr, Map<String, Object> args, boolean fixDash, String downloadName) {
        // 纯下载型文件传输post（multipart/json格式，request参数仅包含普通文本参数，response为单一纯文件）
        HttpURLConnection connection = null;
        String response;
        int code;
        String pathName = Environment.getExternalStorageDirectory().getPath() + Constant.downloadPath;

        String jsonString = JSON.toJSONString(args);
        byte[] argsByte = jsonString.getBytes();

        Log.i("post url", urlStr + (fixDash ? "/" : ""));
        Log.i("post body", jsonString);

        try {
            URL url = new URL(urlStr + (fixDash ? "/" : ""));
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(Constant.connectTimeout);
            connection.setReadTimeout(Constant.readTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "utf-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(argsByte, 0, argsByte.length);
            outputStream.flush();

            code = connection.getResponseCode();
            if (code != 200) {
                Log.w("NetUtil", "post ret code: " + String.valueOf(code));
            }
            InputStream in = connection.getInputStream(); // 得到网络返回的正确输入流
            Map<String, List<String>> responseHeader = connection.getHeaderFields();
//            List<String> Content_Disposition = responseHeader.get("Content-Disposition");
//            String fileName = Objects.requireNonNull(responseHeader.get("Content-Disposition")).get(1);
            FileOutputStream fileOutput = new FileOutputStream(pathName + downloadName);
            byte[] buf = new byte[5 * 1024 * 1024]; //分配缓冲区
            int readNum = 0;
            while ((readNum = in.read(buf)) >= 0) {
                fileOutput.write(buf, 0, readNum);
            }
            fileOutput.close();
            in.close();
            Log.i("response", "file response" + downloadName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        Map<String, String> resMap = new HashMap<>();
        resMap.put("code", String.valueOf(code));
        if (code < 300) {
            resMap.put("file", pathName + downloadName);
        }
        return resMap;
    }

    public static String getUrlBase() {
        return urlBase;
    }

    public static String hexKey2bin(String keyStr) {
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
        if (jsonArray.size() == 0) {
            return new int[][][]{};
        }
        JSONArray mat = (JSONArray) jsonArray.get(0);
        JSONArray line = (JSONArray) mat.get(0);
        int[][][] arr = new int[jsonArray.size()][mat.size()][line.size()];
        for (int k = 0; k < jsonArray.size(); k++) {
            mat = (JSONArray) jsonArray.get(k);
            if (mat == null) {
                Log.i("arrayJson2java", String.format("got null jsonArray at (%d)", k));
                return null;
            }
            for (int i = 0; i < mat.size(); i++) {
                line = (JSONArray) mat.get(i);
                if (line == null) {
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
