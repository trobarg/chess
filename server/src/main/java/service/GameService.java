package service;

import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import service.ServiceException; //necessary when in the same package?
import model.GameData;
import model.AuthData;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<GameData> listGames() {}
    public GameData createGame() {}
    public void joinGame() {}
}
