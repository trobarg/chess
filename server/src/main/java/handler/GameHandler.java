package handler;

import com.google.gson.Gson;
import java.util.Random;
import spark.*;
import model.*;
import service.GameService;

public class GameHandler {
private final GameService gameService = new GameService();
    public Object listGames() {
        var listGamesRequest = new Gson().fromJson(req.body(), ListGamesRequest.class);//need to get authToken from header!?
        return new Gson().toJson(gameService.listGames(listGamesRequest));
    }
    public Object createGame() {
        var createGameRequest = new Gson().toJson(req.body(), CreateGameRequest.class);
        return new Gson().toJson(gameService.createGame(createGameRequest));
    }
    public Object joinGame() {

    }
}
