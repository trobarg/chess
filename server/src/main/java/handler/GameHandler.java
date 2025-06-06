package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import spark.*;
import model.*;
import service.GameService;

public class GameHandler {
    private final GameService gameService;
    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    public Object listGames(Request req, Response res) throws ResponseException {
        RequestWithAuth listGamesRequest = new RequestWithAuth(req.headers("authorization"));//only safe because nothing from body
        GamesList games = new GamesList(gameService.listGames(listGamesRequest));
        res.status(200);
        return new Gson().toJson(games);
    }
    public Object createGame(Request req, Response res) throws ResponseException {
        String combined = "{"; //this is a mess, but I wanted CreateGameRequest to include authToken as a field
        if (!req.headers("authorization").equals("{}")) {
            combined += "\"authToken\":\"" + req.headers("authorization") + "\"";
            if (!req.body().equals("{}")) {
                combined += ",";
            }
        }
        if (!req.body().equals("{}")) {
            combined += req.body().substring(1, req.body().length() - 1);
        }
        combined += "}";
        CreateGameRequest createGameRequest = new Gson().fromJson(combined, CreateGameRequest.class);
        if (createGameRequest.gameName() == null) {
            throw new ResponseException(400, "Error: Bad request");
        }
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        res.status(200);
        return new Gson().toJson(createGameResult);
    }
    public Object joinGame(Request req, Response res) throws ResponseException {
        String combined = "{"; //also a mess, but I wanted JoinGameRequest to include authToken as a field
        if (!req.headers("authorization").equals("{}")) {
            combined += "\"authToken\":\"" + req.headers("authorization") + "\"";
            if (!req.body().equals("{}")) {
                combined += ",";
            }
        }
        if (!req.body().equals("{}")) {
            combined += req.body().substring(1, req.body().length() - 1);
        }
        combined += "}";
        JoinGameRequest joinGameRequest = new Gson().fromJson(combined, JoinGameRequest.class);
        if (joinGameRequest.playerColor() == null || joinGameRequest.gameID() <= 0) {
            throw new ResponseException(400, "Error: Bad request");
        }
        gameService.joinGame(joinGameRequest);
        res.status(200);
        return new Gson().toJson(new EmptyResult());
    }
}
