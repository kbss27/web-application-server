package webserver;

import java.io.*;
import java.net.Socket;

import controller.Controller;
import controller.mapper.ControllerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);

            Controller controller = ControllerMapper.getController(httpRequest.getPath());

            if(controller == null) {
                httpResponse.forward(getDefaultPath(httpRequest.getPath()));
                return;
            }

            controller.service(httpRequest, httpResponse);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        if("/".equals(path)) {
            return "/index.html";
        }
        return path;
    }
}
