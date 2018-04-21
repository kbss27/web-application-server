package webserver;

import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private InputStream in;
    private String method;
    private String path;
    private Map<String, String> headerMap;
    private Map<String, String> parameterInfo;

    public HttpRequest(InputStream in) throws IOException{
        this.in = in;
        parseRequestData();
    }

    public String getMethod() throws IOException {

        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String headerKey) {
        String headerValue = headerMap.get(headerKey);
        return headerValue;
    }

    public String getParameter(String parameter) {
        return parameterInfo.get(parameter);
    }

    private void parseRequestData() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        String[] firstReqLine = HttpRequestUtils.getFirstReqLine(line);
        this.method = firstReqLine[0];
        this.path = firstReqLine[1];
        this.headerMap = getHeaderMap(br, line);
        if (this.headerMap == null) {
            return;
        }

        if("GET".equals(method)) {
            int idx = path.indexOf('?');
            String queryString = path.substring(idx+1);
            path = path.substring(0, idx);
            parameterInfo = HttpRequestUtils.parseQueryString(queryString);

        } else {
            String body = IOUtils.readData(br, Integer.parseInt(headerMap.get("Content-Length")));
            path = firstReqLine[1];
            parameterInfo = HttpRequestUtils.parseQueryString(body);
        }
    }

    private Map<String, String> getHeaderMap(BufferedReader br, String line) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        while (!"".equals(line)) {
            if (line == null) {
                return null;
            }
            String[] header = line.split(" ");
            int idx = header[0].indexOf(":");
            if (idx > 0) {
                headerMap.put(header[0].substring(0, idx), header[1]);
            }
            line = br.readLine();
        }
        return headerMap;
    }


}
