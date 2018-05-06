package controller;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.Collection;
import java.util.Map;

public class ListUserControllerImpl extends ControllerImpl {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        Map<String, String> getCookies = HttpRequestUtils.parseCookies(request.getHeader("Cookie"));

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

            response.forwardBody(sb.toString());
        } else {
            response.forward("/user/login.html");
        }
    }
}
