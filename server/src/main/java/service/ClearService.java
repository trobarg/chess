package service;

import dataaccess.*;
import model.*;

public class ClearService {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ClearApplicationResult clearApplication(ClearApplicationRequest clearApplicationRequest) throws DataAccessException {
        userDAO.clearUsers();
        gameDAO.clearGames();
        authDAO.clearAuths();
        return new ClearApplicationResult();
    }
}
