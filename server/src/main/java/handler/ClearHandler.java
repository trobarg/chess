package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.ClearApplicationRequest;
import model.RequestWithAuth;
import service.ResponseException;
import spark.*;
import service.ClearService;

public class ClearHandler {
    private final ClearService clearService;
    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }
    public Object clearApplication (Request req, Response res) throws ResponseException, DataAccessException {
        var clearApplicationRequest = new Gson().fromJson((req.headers("authorization")), ClearApplicationRequest.class);
        return new Gson().toJson(clearService.clearApplication(clearApplicationRequest));
    }
}
