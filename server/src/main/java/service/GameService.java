package service;

import java.util.HashSet;

import dataaccess.*;
import model.GameData;
import model.AuthData;

import java.util.Collection;
import java.util.Random;

public class GameService {
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();


    public Collection<GameData> listGames(AuthData auth) throws ServiceException {
        try {
            if (authDAO.getAuthByAuthToken(auth.authToken()) == null) {
                throw new ServiceException(401, "Error: Unauthorized");
            }
            else {
                return gameDAO.listGames();
            }
        }
        catch (DataAccessException dAE) {
            throw new ServiceException(500, dAE.getMessage());
        }
    }
    public GameData createGame(GameData game, AuthData auth) throws ServiceException { //game already contains gameID from elsewhere?
        try {
            if (authDAO.getAuthByAuthToken(auth.authToken()) == null) {
                throw new ServiceException(401, "Error: Unauthorized");
            }
            else {
                Random rand = new Random();
                int gameID = rand.nextInt(1000000);
                HashSet<Integer> keys = (HashSet<Integer>) gameDAO.getGameIDs();
                do {

                }
                while();
            }
        }
        catch (DataAccessException dAE) {
            throw new ServiceException(500, dAE.getMessage());
        }
    }
    public void joinGame() throws ServiceException {
        try {

        }
        catch (DataAccessException dAE) {
            throw new ServiceException(500, dAE.getMessage());
        }
    }
}
