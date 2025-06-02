package service;

import dataaccess.*;
import exception.ResponseException;
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

    public EmptyResult clearApplication(ClearApplicationRequest clearApplicationRequest) throws ResponseException {
        try {
            userDAO.clearUsers();
            gameDAO.clearGames();
            authDAO.clearAuths();
            return new EmptyResult();
        }
        catch (DataAccessException exception) {
            throw new ResponseException(500, "Error: " + exception.getMessage());
        }
    }
}
