package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

public class ControllerImpl implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String method = request.getMethod();
        if ("GET".equals(method)) {
            doGet(request, response);
        } else if ("POST".equals(method)) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {}

    protected void doGet(HttpRequest request, HttpResponse response) {}
}
