package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;

import com.sun.deploy.util.StringUtils;
import controller.Controller;
import controller.CreateUserController;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private Map<String, Controller> controllerMap;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            controllerMap = new HashMap<>();

            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);

            //GET인지 POST인지부터 구별해야한다.
            if ("GET".equals(httpRequest.getMethod())) {
                if(httpRequest.getPath().startsWith("/user/list")) {
                    Map<String, String> getCookies = HttpRequestUtils.parseCookies(httpRequest.getHeader("Cookie"));

                    //logined되어있는 계정인지 쿠키 확인
                    if(Boolean.parseBoolean(getCookies.get("logined"))) {
                        Collection<User> users = DataBase.findAll();
                        StringBuilder sb = new StringBuilder();
                        sb.append("<table border='1'>");
                        for(User user : users) {
                            sb.append("<tr>");
                            sb.append(" <th>#</th> <th>사용자 아이디</th> <th>이름</th> <th>이메일</th><th></th>");
                            sb.append("</tr>");
                            sb.append("<tr>");
                            sb.append("<th>1</th> <td>"+user.getUserId()+"</td> <td>"+user.getName()+"</td> <td>"+user.getEmail()+"</td>");
                            sb.append("</tr>");
                        }
                        sb.append("</table>");

                        httpResponse.forwardBody(sb.toString());
                    } else {
                        httpResponse.forward("/user/login.html");
                        return;
                    }
                } else if(httpRequest.getPath().startsWith("/css/style")) {
                    httpResponse.forward(httpRequest.getPath());
                }
                httpResponse.forward(httpRequest.getPath());
            } else if ("POST".equals(httpRequest.getMethod())) {
                if (httpRequest.getPath().startsWith("/user/create")) {
                    User user = new User(httpRequest.getParameter("userId"), httpRequest.getParameter("password"),
                            httpRequest.getParameter("name"), httpRequest.getParameter("email"));
                    DataBase.addUser(user);
                    httpResponse.sendRedirect("/index.html");

                } else if (httpRequest.getPath().startsWith("/user/login")) {
                    User otherUser = new User(httpRequest.getParameter("userId"), httpRequest.getParameter("password"),
                            httpRequest.getParameter("name"), httpRequest.getParameter("email"));
                    User getUser = DataBase.findUserById(otherUser.getUserId());
                    if(getUser != null) {
                        if(getUser.equals(otherUser)) {
                            //로그인 성공시 응답 헤더에 cookie를 추가해 로그인 성공 여부 전달
                            httpResponse.addHeader("Set-Cookie", "logined=true");
                            httpResponse.sendRedirect("/index.html");

                        } else {
                            //로그인 실패시 cookie false로 설정
                            httpResponse.addHeader("Set-Cookie", "logined=false");
                            httpResponse.sendRedirect("/index.html");
                        }
                    } else {
                        httpResponse.sendRedirect("/user/login_failed.html");
                    }
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
