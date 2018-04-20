package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;

import com.sun.deploy.util.StringUtils;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private User user;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
//            byte[] body;

            DataOutputStream dos = new DataOutputStream(out);

            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = br.readLine();
            String[] firstReqLine = HttpRequestUtils.getFirstReqLine(line);
            String method = firstReqLine[0];
            String getURL = firstReqLine[1];


            //request header를 읽으면서 ~: ~ 형식을 key value로 mapping 시킨다
            Map<String, String> headerMap = getHeaderMap(br, line);
            if (headerMap == null) {
                return;
            }

            //GET인지 POST인지부터 구별해야한다.
            //Request마다 thread를 생성해서 해당 request를 처리하는 stateless상태 때문에 user 객체를 가지고 있겠다라는 생각은 잘못된것!
            if ("GET".equals(method)) {
                if(getURL.startsWith("/user/list")) {
                    Map<String, String> getCookies = HttpRequestUtils.parseCookies(headerMap.get("Cookie"));
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

                        byte[] body = sb.toString().getBytes();
                        response200Header(dos, body.length);
                        responseBody(dos, body);
                    } else {
                        responseTerminal(dos, "/user/login.html");
                        return;
                    }
                } else if(getURL.startsWith("/css/style")) {
                    byte[] body = Files.readAllBytes(new File("./webapp"+getURL).toPath());
                    response200HeaderWithCSS(dos, body.length);
                    responseBody(dos, body);
                }
                responseTerminal(dos, getURL);
            } else if ("POST".equals(method)) {
                if (getURL.startsWith("/user/create")) {
                    String body = IOUtils.readData(br, Integer.parseInt(headerMap.get("Content-Length")));
                    Map<String, String> userInfo = HttpRequestUtils.parseQueryString(body);
                    user = new User(userInfo);
                    log.debug("User Info : {}", user);
                    DataBase.addUser(user);
                    response302Header(dos, "index.html");
                } else if (getURL.startsWith("/user/login")) {
                    String body = IOUtils.readData(br, Integer.parseInt(headerMap.get("Content-Length")));
                    Map<String, String> userInfo = HttpRequestUtils.parseQueryString(body);
                    User otherUser = new User(userInfo);
                    User getUser = DataBase.findUserById(otherUser.getUserId());
                    if(getUser != null) {
                        if(getUser.equals(otherUser)) {
                            //로그인 성공시 응답 헤더에 cookie를 추가해 로그인 성공 여부 전달
                            byte[] responseBody = Files.readAllBytes(new File("./webapp"+"/index.html").toPath());
                            response200HeaderWithCookie(dos, responseBody.length, "true");
                            responseBody(dos, responseBody);
                        } else {
                            //로그인 실패시 cookie false로 설정
                            byte[] responseBody = Files.readAllBytes(new File("./webapp"+"/user/login_failed.html").toPath());
                            response200HeaderWithCookie(dos, responseBody.length, "false");
                            responseBody(dos, responseBody);
                        }
                    } else {
                        response302Header(dos, "user/login_failed.html");
                    }
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
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

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }ß

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
            dos.writeBytes("Location: http://localhost:8080/"+redirectUrl);
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

    //////////////////////////////////////////////////////////////////
    //private Method
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

    private void responseTerminal(DataOutputStream dos, String urlPath) throws IOException {
        byte[] responseBody = Files.readAllBytes(new File("./webapp"+urlPath).toPath());
        response200Header(dos, responseBody.length);
        responseBody(dos, responseBody);
    }
}
