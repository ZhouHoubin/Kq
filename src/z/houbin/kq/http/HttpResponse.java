package z.houbin.kq.http;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

public class HttpResponse {
    public String urlString;
    public int defaultPort;
    public String file;
    public String host;
    public String path;
    public int port;
    public String protocol;
    public String query;
    public String ref;
    public String userInfo;
    public String content;
    public String contentEncoding;
    public int code;
    public String message;
    public String contentType;
    public String method;
    public int connectTimeout;
    public int readTimeout;
    public Vector<String> contentCollection;
    public String location;
    public List<String> cookies;

    public String findCookie(String key) {
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith(key)) {
                    String val = null;
                    try {
                        val = cookie.split(";")[0].split("=")[1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return val;
                }
            }
        }
        return null;
    }

    public LinkedHashMap<String, String> getCookieMap() {
        LinkedHashMap<String, String> maps = new LinkedHashMap<>();
        if (cookies != null) {
            for (String cookie : cookies) {
                String line = cookie.split(";")[0];
                if (line == null) {
                    continue;
                }
                String lines[] = line.split("=");
                if (lines.length != 2 || lines[1] == null || "".equalsIgnoreCase(lines[1])) {
                    continue;
                }
                maps.put(lines[0], lines[1]);
            }
        }
        return maps;
    }

    public String getCookieString() {
        StringBuilder builder = new StringBuilder();
        if (cookies != null) {
            for (String cookie : cookies) {
                builder.append(cookie.split(";")[0]);
                builder.append(";");
            }
        }
        return builder.toString();
    }
}
