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

    public AuthData register(String username, String password, String email) throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        AuthData response = httpCommunicator.makeRequest("POST", "/user", registerRequest, null, AuthData.class);
        authToken = response.authToken();
        return response;
    }
    public AuthData login(String username, String password) throws ResponseException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        AuthData response = httpCommunicator.makeRequest("POST", "/session", loginRequest, null, AuthData.class);
        authToken = response.authToken();
        return response;
    }

    public void logout() throws ResponseException {
        httpCommunicator.makeRequest("DELETE", "/session", null, authToken, null);
        authToken = null;
    }

    public HashSet<GameData> listGames() throws ResponseException {
        record GamesWrapper(HashSet<GameData> games) {
        }
        GamesWrapper response = httpCommunicator.makeRequest("GET", "/game", null, authToken, GamesWrapper.class);
        return response.games();
    }

    public CreateGameResult createGame(String gameName) throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest(null, gameName); //this could be hard to deserialize correctly
        return httpCommunicator.makeRequest("POST", "/game", createGameRequest, authToken, CreateGameResult.class);
    }

    public void joinGame(int gameID, String color) throws ResponseException {
        JoinGameRequest joinGameRequest = new JoinGameRequest(null, gameID, color);
        httpCommunicator.makeRequest("PUT", "/game", joinGameRequest, authToken, null);
    }

}
