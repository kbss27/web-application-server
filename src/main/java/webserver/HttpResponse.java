package webserver;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    OutputStream out;
    Map<String, String> responseHeader = new HashMap<String, String>();

    public HttpResponse(OutputStream out) {
        this.out = out;
    }

    public void forward(String path) throws IOException {
        byte[] responseBody = Files.readAllBytes(new File("./webapp"+path).toPath());
        DataOutputStream dos = new DataOutputStream(out);
        responseHeader.put("Content-Type", "text/html;charset=utf-8");
        responseHeader.put("Content-Length", String.valueOf(responseBody.length));
        response200Header(dos);
        responseBody(dos, responseBody);
    }

    public void sendRedirect(String path) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        response302Header(dos, path);
    }

    public void addHeader(String key, String value) {
        responseHeader.put(key, value);
    }


    private void response200HeaderWithCSS(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            for(String key : responseHeader.keySet()) {
                dos.writeBytes(key + ": " + responseHeader.get(key)+"\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200HeaderWithCookie(DataOutputStream dos, int lengthOfBodyContent, String logined) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Set-Cookie: logined="+logined+"\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND\r\n");
            for(String key : responseHeader.keySet()) {
                dos.writeBytes(key + ": " + responseHeader.get(key)+"\r\n");
            }
            dos.writeBytes("Location: http://localhost:8080"+redirectUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
