package handler;

import com.google.gson.Gson;
import service.ResponseException;
import spark.*;
import model.*;
import service.UserService;

public class UserHandler {
    private final UserService userService;
    public UserHandler(UserService userService) {
        this.userService = userService;
    }
    public Object register(Request req, Response res) throws ResponseException {
        var userData = new Gson().fromJson(req.body(), UserData.class);
        return new Gson().toJson(userService.register(userData));
    }
    public Object login(Request req, Response res) throws ResponseException {
        var loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        return new Gson().toJson(userService.login(loginRequest));
    }
    public Object logout(Request req, Response res) throws ResponseException {
        var logoutRequest = new Gson().fromJson(req.headers("authorization"), RequestWithAuth.class);
        userService.logout(logoutRequest);
        return new Gson().toJson(null);//potential to cause problems
    }
}
