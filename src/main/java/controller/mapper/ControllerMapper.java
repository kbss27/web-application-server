package controller.mapper;

import controller.Controller;
import controller.Implement.CreateUserControllerImpl;
import controller.Implement.ListUserControllerImpl;
import controller.Implement.LoginControllerImpl;

import java.util.HashMap;
import java.util.Map;

public class ControllerMapper {
    private static Map<String, Controller> controllerMap = new HashMap<>();

    /**
     * URL에 대한 요구사항이 추가될때마다 여기에 추가하면 된다.
     */
    static {
        controllerMap.put("/user/list", new ListUserControllerImpl());
        controllerMap.put("/user/create", new CreateUserControllerImpl());
        controllerMap.put("/user/login", new LoginControllerImpl());
    }

    public static Controller getController(String url) {
        return controllerMap.get(url);
    }

}
