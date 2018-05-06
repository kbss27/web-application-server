package controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {
    private static Map<String, Controller> controllerMap = new HashMap<>();

    static {
        controllerMap.put("/user/list", new ListUserController());
        controllerMap.put("/user/create", new CreateUserController());
        controllerMap.put("/user/login", new LoginController());
    }

    public static Controller getController(String url) {
        return controllerMap.get(url);
    }

}
