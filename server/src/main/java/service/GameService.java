package service;
import java.util.HashSet;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.Collection;
import java.util.Random;

public class GameService {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    //Potential problems with different services not using the same DAOs?
    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }
    public Collection<GameData> listGames(RequestWithAuth listGamesRequest) throws ResponseException {
        try {
            if (authDAO.getAuthByAuthToken(listGamesRequest.authToken()) == null) {
                throw new ResponseException(401, "Error: Unauthorized");
            }
            else {
                return gameDAO.listGames();
            }
        }
        catch (DataAccessException dAE) {
            throw new ResponseException(500, dAE.getMessage());
        }
    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws ResponseException {
        try {
            if (authDAO.getAuthByAuthToken(createGameRequest.authToken()) == null) {
                throw new ResponseException(401, "Error: Unauthorized");
            }
            else {//check against other games by the same name? error code 400 bad request?
                Random rand = new Random();
                int bound = 1000;
                HashSet<Integer> keys = (HashSet<Integer>) gameDAO.getGameIDs();
                while (keys.size() * 10 >= bound) {
                    bound *= 10;
                }
                int gameID = rand.nextInt(bound + 1);
                while (keys.contains(gameID)) {
                    gameID = rand.nextInt(bound + 1);
                }
                GameData gameData = new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame());
                return new CreateGameResult(gameDAO.addGame(gameData).gameID());//these two lines could be expanded for readability
            }
        }
        catch (DataAccessException dAE) {
            throw new ResponseException(500, dAE.getMessage());
        }
    }
    public void joinGame(JoinGameRequest joinGameRequest) throws ResponseException {//null return type?
        try {
            if (authDAO.getAuthByAuthToken(joinGameRequest.authToken()) == null) {
                throw new ResponseException(401, "Error: Unauthorized");
            }
            else {
                if (joinGameRequest.playerColor().equals("WHITE")) {
                    if (gameDAO.getGameByID(joinGameRequest.gameId()).whiteUsername() != null) {
                        throw new ResponseException(403, "Error: Already taken");
                    }

                }
                else if (joinGameRequest.playerColor().equals("BLACK")) {
                    if (gameDAO.getGameByID(joinGameRequest.gameId()).blackUsername() != null) {
                        throw new ResponseException(403, "Error: Already taken");
                    }
                }
            }
        }
        catch (DataAccessException dAE) {
            throw new ResponseException(500, dAE.getMessage());
        }
    }
}
