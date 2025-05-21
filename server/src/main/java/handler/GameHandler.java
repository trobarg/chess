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
        RequestWithAuth listGamesRequest = new RequestWithAuth(req.headers("authorization"));
        GamesList games = new GamesList(gameService.listGames(listGamesRequest));
        res.status(200);
        return new Gson().toJson(games);
    }
    public Object createGame(Request req, Response res) throws ResponseException {
        String authToken = req.headers("authorization");
        String body = req.body();
        var intermediate = new Gson().toJson(authToken + body);
        var createGameRequest = new Gson().fromJson(intermediate, CreateGameRequest.class);
        return new Gson().toJson(gameService.createGame(createGameRequest));
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
