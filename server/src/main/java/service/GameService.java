package service;

import chess.ChessGame;
import dataaccess.*;
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
            throw new ResponseException(500, dAE.getMessage());
        }
    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws ResponseException {
        try {
            if (authDAO.getAuthByAuthToken(createGameRequest.authToken()) == null) {
                throw new ResponseException(401, "Error: Unauthorized");
            }
            else {//check against other games by the same name? error code 400 bad request?
                //might have to drop this gameID generating logic for the tests
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
                //int gameID = gameDAO.getGameIDs().size() - 1;
                new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame());
                gameDAO.addGame(new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame()));
                return new CreateGameResult(gameID); //not getting gameID from DAO
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
                    if (gameDAO.getGameByID(joinGameRequest.gameID()).whiteUsername() != null) {
                        throw new ResponseException(403, "Error: Already taken");
                    }
                    else {
                        String playerUsername = authDAO.getAuthByAuthToken(joinGameRequest.authToken()).username();
                        GameData current = gameDAO.getGameByID(joinGameRequest.gameID());
                        GameData updated = new GameData(current.gameID(), playerUsername, current.blackUsername(), current.gameName(), current.game());
                        gameDAO.updateGame(updated);
                    }
                }
                else if (joinGameRequest.playerColor().equals("BLACK")) {
                    if (gameDAO.getGameByID(joinGameRequest.gameID()).blackUsername() != null) {
                        throw new ResponseException(403, "Error: Already taken");
                    }
                    else {
                        String playerUsername = authDAO.getAuthByAuthToken(joinGameRequest.authToken()).username();
                        GameData current = gameDAO.getGameByID(joinGameRequest.gameID());
                        GameData updated = new GameData(current.gameID(), current.whiteUsername(), playerUsername, current.gameName(), current.game());
                        gameDAO.updateGame(updated);
                    }
                }
            }
        }
        catch (DataAccessException dAE) {
            throw new ResponseException(500, dAE.getMessage());
        }
    }
}
