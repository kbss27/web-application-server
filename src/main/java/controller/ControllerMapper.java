package controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {
    private static Map<String, Controller> controllerMap = new HashMap<>();

    static {
        controllerMap.put("/user/list", new ListUserControllerImpl());
        controllerMap.put("/user/create", new CreateUserControllerImpl());
        controllerMap.put("/user/login", new LoginControllerImpl());
    }

    public static Controller getController(String url) {
        return controllerMap.get(url);
    }

}
