package client;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.util.Map;

public class ServerFacade {
    private final String serverURL;
    private final HTTPCommunicator httpCommunicator;
    private String authToken;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
        this.httpCommunicator = new HTTPCommunicator(serverURL, this);
    }

    public void register(String username, String password, String email) throws ResponseException {//not sure what to return
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        AuthData response = httpCommunicator.makeRequest("POST", "/user", registerRequest, AuthData.class);
        authToken = response.authToken();
    }
    public void login(String username, String password) throws ResponseException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        AuthData response = httpCommunicator.makeRequest("POST", "/session", loginRequest, AuthData.class);
        authToken = response.authToken();
    }
}
