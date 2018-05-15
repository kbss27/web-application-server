package controller;

import enums.HttpMethod;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class ControllerImpl implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod method = request.getMethod();
        if (method.isGet()) {
            doGet(request, response);
        } else if (method.isPost()) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {}

    protected void doGet(HttpRequest request, HttpResponse response) {}
}
