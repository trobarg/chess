package handler;

import com.google.gson.Gson;


import model.LoginRequest;
import model.LogoutRequest;
import model.RegisterRequest;
import model.RegisterResult;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import service.UserService;

public class UserHandler {
    private final UserService userService = new UserService();

    public Object Register(Request req, Response res) {
        var registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        var registerResult = userService.register(registerRequest);
        return new Gson().toJson(registerResult);
    }
    public Object Login(Request req, Response res) {
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        var loginResult = userService.login(loginRequest);
        return new Gson().toJson(loginResult);
    }
    public Object Logout(Request req, Response res) {
        var logoutRequest = new Gson().fromJson(req.body(), LogoutRequest.class); //need to get authToken from header!?
        var logoutResult = userService.logout(logoutRequest);
        return new Gson().toJson(logoutResult);

    }
}
