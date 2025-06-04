package client;

import exception.ResponseException;
import model.*;

import java.util.HashSet;

public class ServerFacade {
    private final String serverURL;
    private final HTTPCommunicator httpCommunicator;
    private String authToken;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
        this.httpCommunicator = new HTTPCommunicator(serverURL, this);
    }
    //methods should take in a request object and return a result object?
    public void register(String username, String password, String email) throws ResponseException {//return a RegisterResult?
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        AuthData response = httpCommunicator.makeRequest("POST", "/user", registerRequest, AuthData.class);
        authToken = response.authToken();
    }
    public void login(String username, String password) throws ResponseException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        AuthData response = httpCommunicator.makeRequest("POST", "/session", loginRequest, AuthData.class);
        authToken = response.authToken();
    }

    public void logout() throws ResponseException {
        RequestWithAuth requestWithAuth = new RequestWithAuth(authToken);
        httpCommunicator.makeRequest("DELETE", "/session", requestWithAuth, null);//how will this get the authToken into the header?
        authToken = null;
    }

    public HashSet<GameData> listGames() throws ResponseException {
        RequestWithAuth requestWithAuth = new RequestWithAuth(authToken);
        record ListGamesResponse(HashSet<GameData> games) {
        }
        ListGamesResponse response = httpCommunicator.makeRequest("GET", "/game", requestWithAuth, ListGamesResponse.class); //need a wrapper class for the HashSet?
        return response.games();
    }

    public CreateGameResult createGame(String gameName) throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
        return httpCommunicator.makeRequest("POST", "/game", createGameRequest, CreateGameResult.class);
    }

    public void joinGame(int gameID, String color) throws ResponseException {
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, gameID, color); //mismatched order of variables
        httpCommunicator.makeRequest("PUT", "/game", joinGameRequest, null);
    }

}
