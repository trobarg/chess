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
        ClearApplicationRequest clearApplicationRequest = new ClearApplicationRequest();
        return new Gson().toJson(clearService.clearApplication(clearApplicationRequest));
    }
}
