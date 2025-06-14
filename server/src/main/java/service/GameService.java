package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.*;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

public class GameService {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
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
            throw new ResponseException(500, "Error: " + dAE.getMessage());
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
                Set<Integer> keys = (Set<Integer>) gameDAO.getGameIDs(); //not casting to HashSet<Integer> specifically
                while (keys.size() * 10 >= bound) {
                    bound *= 10;
                }
                int gameID = rand.nextInt(bound + 1);
                while (keys.contains(gameID)) {
                    gameID = rand.nextInt(bound + 1);
                }
                new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame());
                gameDAO.addGame(new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame()));
                return new CreateGameResult(gameID); //not getting gameID from DAO
            }
        }
        catch (DataAccessException dAE) {
            throw new ResponseException(500, "Error: " + dAE.getMessage());
        }
    }
    public void joinGame(JoinGameRequest joinGameRequest) throws ResponseException {
        try {
            if (authDAO.getAuthByAuthToken(joinGameRequest.authToken()) == null) {
                throw new ResponseException(401, "Error: Unauthorized");
            }
            else {
                GameData current = gameDAO.getGameByID(joinGameRequest.gameID());
                String username = authDAO.getAuthByAuthToken(joinGameRequest.authToken()).username();
                if (joinGameRequest.playerColor().equalsIgnoreCase("WHITE")) {
                    if (current.whiteUsername() != null && !current.whiteUsername().equals(username)) {
                        throw new ResponseException(403, "Error: Already taken");
                    }
                    else {
                        GameData updated = new GameData(current.gameID(), username, current.blackUsername(),
                                current.gameName(), current.game());
                        gameDAO.updateGame(updated);
                    }
                }
                else if (joinGameRequest.playerColor().equalsIgnoreCase("BLACK")) {
                    if (current.blackUsername() != null && !current.blackUsername().equals(username)) {
                        throw new ResponseException(403, "Error: Already taken");
                    }
                    else {
                        GameData updated = new GameData(current.gameID(), current.whiteUsername(), username,
                                current.gameName(), current.game());
                        gameDAO.updateGame(updated);
                    }
                }
                else {
                    throw new ResponseException(400, "Error: Bad request");
                }
            }
        }
        catch (DataAccessException dAE) {
            throw new ResponseException(500, "Error: " + dAE.getMessage());
        }
    }
}
