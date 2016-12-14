package autotest.utils;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class HttpUtils {
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getMd5(String value, String charset) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(value.getBytes(charset));
            return toHexString(md5.digest());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static String convertParams(HashMap<String, Object> params) {
        try {

            StringBuilder sb = new StringBuilder();

            if (params != null && params.size() > 0) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    Object value = entry.getValue();
                    value = value == null ? "" : value;
                    sb.append(entry.getKey()).append("=")
                            .append(URLEncoder.encode(String.valueOf(value), "UTF-8")).append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getKyxKey(String params) {
        String first = getMd5("api.kuaiyouxi.com@youxikyxlaile", "UTF-8");
        String second = params;
        String key = getMd5(first + second, "UTF-8");
        return key;
    }

    public static String createGetUrlNoBaseParams(String url, HashMap<String, Object> params) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }

        String paramString = convertParams(params);
        return createGetUrl(url, paramString);
    }

    public static String createGetUrl(String url, String paramString) {
        paramString += "&key=" + getKyxKey(paramString);
        return url + "?" + paramString;
    }

    /**
     * 用于post提交方式得key
     *
     * @param gContent 要进行加密的{@link}
     * @return key值
     */
    public static String getKyxKey4Post(String gContent) {
        return getMd5(gContent + "api.kuaiyouxi.com@youxikyxlaile", "UTF-8");
    }

    public static String getKyxPostKey() {
        String first = getMd5("api.kuaiyouxi.com@youxikyxlaile", "UTF-8");
        return first;
    }

    /**
     * get请求处理
     *
     * @return
     */
    public static  <T> T getRequest(String url, Type type) throws Exception {
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(15000);

            return doResponse(conn, type);
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Throwable e) {
                }
            }
        }
    }

    private static <T> T doResponse(HttpURLConnection conn, Type type) throws Exception {
        InputStream ins = null;
        T result = null;

        try {
            ins = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            String encoding = conn.getHeaderField("Content-Encoding");
            if (encoding != null && !encoding.trim().equals("")) {
                ins = new GZIPInputStream(ins);
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            in2out(ins, os);
            byte[] buff = os.toByteArray();
            result = processContent(buff, type);
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (Throwable e) {
                }
            }
        }

        return result;
    }
    
    private static <T> T processContent(byte[] buffers, Type type) {
        try {
            String content = new String(buffers, "UTF-8");

            Gson gson = new Gson();

            T result = gson.fromJson(content, type);
            return result;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    
    private static void in2out(InputStream ins, OutputStream os) throws Exception {
        byte[] buff = new byte[1024];
        int len;
        while ((len = ins.read(buff)) != -1) {
            os.write(buff, 0, len);
        }
    }
}
