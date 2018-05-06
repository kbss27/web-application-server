package controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class LoginControllerImpl extends ControllerImpl {

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User otherUser = new User(request.getParameter("userId"), request.getParameter("password"),
                request.getParameter("name"), request.getParameter("email"));
        User getUser = DataBase.findUserById(otherUser.getUserId());

        if (getUser != null) {
            if(getUser.equals(otherUser)) {
                //로그인 성공시 응답 헤더에 cookie를 추가해 로그인 성공 여부 전달
                response.addHeader("Set-Cookie", "logined=true");
                response.sendRedirect("/index.html");

            } else {
                //로그인 실패시 cookie false로 설정
                response.addHeader("Set-Cookie", "logined=false");
                response.sendRedirect("/index.html");
            }
        } else {
            response.addHeader("Set-Cookie", "logined=false");
            response.sendRedirect("/user/login_failed.html");
        }
    }

}
