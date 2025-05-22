package handler;

import com.google.gson.Gson;
import service.ResponseException;
import spark.*;
import model.*;
import service.GameService;

import java.util.Collection;

public class GameHandler {
    private final GameService gameService;
    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    public Object listGames(Request req, Response res) throws ResponseException {
        RequestWithAuth listGamesRequest = new RequestWithAuth(req.headers("authorization"));//only safe because nothing to deserialize?
        GamesList games = new GamesList(gameService.listGames(listGamesRequest));
        res.status(200);
        return new Gson().toJson(games);
    }
    public Object createGame(Request req, Response res) throws ResponseException {
        String combined = "{";
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
        String authToken = req.headers("authorization");
        String body = req.body();
        var intermediate = new Gson().toJson(authToken + body);
        var joinGameRequest = new Gson().fromJson(intermediate, JoinGameRequest.class);
        gameService.joinGame(joinGameRequest);
        return new Gson().toJson(null);//potential to cause problems
    }
}
