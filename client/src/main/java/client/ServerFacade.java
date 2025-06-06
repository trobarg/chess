package client;

import chess.*;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import websocket.commands.Leave;
import websocket.commands.MakeMove;
import websocket.commands.Resign;
import websocket.commands.UserGameCommand;

import java.util.*;

public class ServerFacade {
    private final String serverURL;
    private final HTTPCommunicator httpCommunicator;
    //private final WebSocketCommunicator webSocketCommunicator;
    private final ArrayList<GameData> games = new ArrayList<>();
    private String authToken;
    /*
    Perhaps by changing the listing logic to not adhere to whatever order the server returns,
    there's a way for a running client to keep track of a game number - ID association
    */

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
        this.httpCommunicator = new HTTPCommunicator(serverURL, this);
        //this.webSocketCommunicator = new WebSocketCommunicator(serverURL, notificationHandler);
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
        GamesList response = refreshGames();
        return response.games();
    }

    public CreateGameResult createGame(String gameName) throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest(null, gameName); //this could be hard to deserialize correctly
        CreateGameResult createGameResult = httpCommunicator.makeRequest("POST", "/game", createGameRequest, authToken, CreateGameResult.class);
        refreshGames(); //probably don't need to refresh here
        return createGameResult;
    }

    public void joinGame(int gameNumber, String color) throws ResponseException {
        refreshGames();
        JoinGameRequest joinGameRequest = new JoinGameRequest(null, getGameDataByNumber(gameNumber).gameID(), color);
        httpCommunicator.makeRequest("PUT", "/game", joinGameRequest, authToken, null);
    }

    public void sendCommand(UserGameCommand command) {
        String message = new Gson().toJson(command);
        //webSocketCommunicator.sendMessage(message);
    }

    public void makeMove(int gameID, ChessMove move) {
        sendCommand(new MakeMove(authToken, gameID, move));
    }

    public void leave(int gameID) { //these are actual gameIDs, not numbers
        sendCommand(new Leave(authToken, gameID));
    }

    public void resign(int gameID) {
        sendCommand(new Resign(authToken, gameID));
    }

    private GamesList refreshGames() throws ResponseException {
        GamesList response = httpCommunicator.makeRequest("GET", "/game", null, authToken, GamesList.class);
        for (GameData game : response.games()) {
            if (!games.contains(game)) {
                games.add(game);
            }
        }
        games.retainAll(response.games());
        return response;
    }
    protected List<GameData> getGames() throws ResponseException {
        refreshGames();
        return games;
    }
    protected GameData getGameDataByNumber (int index) throws ResponseException {
        refreshGames();
        return games.get(index - 1);
    }
}
