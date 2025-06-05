package client;

import exception.ResponseException;
import model.*;

import java.util.*;

public class ServerFacade {
    private final String serverURL;
    private final HTTPCommunicator httpCommunicator;
    private final ArrayList<Integer> gameIDs = new ArrayList<>();
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

    public Collection<GameData> listGames() throws ResponseException {
        GamesList response = refreshGameIDs();
        return response.games();
    }

    public CreateGameResult createGame(String gameName) throws ResponseException {
        refreshGameIDs();
        CreateGameRequest createGameRequest = new CreateGameRequest(null, gameName); //this could be hard to deserialize correctly
        CreateGameResult createGameResult = httpCommunicator.makeRequest("POST", "/game", createGameRequest, authToken, CreateGameResult.class);
        gameIDs.add(createGameResult.gameID());
        return createGameResult;
    }

    public void joinGame(int gameID, String color) throws ResponseException {
        refreshGameIDs();
        JoinGameRequest joinGameRequest = new JoinGameRequest(null, gameIDs.get(gameID), color);
        httpCommunicator.makeRequest("PUT", "/game", joinGameRequest, authToken, null);
    }

    private GamesList refreshGameIDs () throws ResponseException {
        GamesList response = httpCommunicator.makeRequest("GET", "/game", null, authToken, GamesList.class);
        ArrayList<Integer> providedIDs = new ArrayList<>();
        for (GameData game : response.games()) {
            providedIDs.add(game.gameID());
            if (!gameIDs.contains(game.gameID())) {
                gameIDs.add(game.gameID());
            }
        }
        gameIDs.retainAll(providedIDs);
        return response;
    }
    protected ArrayList<Integer> getGameIDs() throws ResponseException {
        refreshGameIDs();
        return gameIDs;
    }
}
