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
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new ResponseException(400, "Error: Bad request");
        }
        try {
            AuthData authData = userService.login(loginRequest);
            res.status(200);
            return new Gson().toJson(authData);
        }
        catch (ResponseException rE) {
            res.status(rE.statusCode());
            //return "{ \"message\": \"Error\": " + rE.getMessage() + " }";
        }
        return null;
    }
    public Object logout(Request req, Response res) throws ResponseException {
        var logoutRequest = new Gson().fromJson(req.headers("authorization"), RequestWithAuth.class);
        userService.logout(logoutRequest);
        return new Gson().toJson(null);//potential to cause problems
    }
}
