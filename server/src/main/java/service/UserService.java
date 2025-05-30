package service;
import java.util.UUID;

import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }
    public AuthData register(UserData user) throws ResponseException {
        try {
            if (userDAO.getUserByUsername(user.username()) != null) {
                throw new ResponseException(403, "Error: Username is already in use");
            }
            else {
                userDAO.addUser(user);
                String authToken = UUID.randomUUID().toString();
                authDAO.addAuth(new AuthData(authToken, user.username()));//order of variables is important!
                return authDAO.getAuthByAuthToken(authToken);
            }
        }
        catch (DataAccessException dAE) {
            throw new ResponseException(500, dAE.getMessage());
        }
    }

    public AuthData login(LoginRequest loginRequest) throws ResponseException {
        try {
            if (userDAO.getUserByUsername(loginRequest.username()) == null) {
                throw new ResponseException(401, "Error: Unauthorized");//Not some kind of username not found error?
            }
            else if (!BCrypt.checkpw(loginRequest.password(), userDAO.getUserByUsername(loginRequest.username()).password())) {
                throw new ResponseException(401, "Error: Unauthorized");
            }
            else {
                String authToken = UUID.randomUUID().toString();
                authDAO.addAuth(new AuthData(authToken, loginRequest.username()));//order of variables is important!
                return authDAO.getAuthByAuthToken(authToken);
            }
        }
        catch (DataAccessException dAE) {
            throw new ResponseException(500, dAE.getMessage());
        }
    }

    public void logout(RequestWithAuth logoutRequest) throws ResponseException {
        try {
            if (authDAO.getAuthByAuthToken(logoutRequest.authToken()) == null) {
                throw new ResponseException(401, "Error: Unauthorized");
            }
            else {
                authDAO.deleteAuthByAuthToken(logoutRequest.authToken());
            }
        }
        catch (DataAccessException dAE) {
            throw new ResponseException(500, dAE.getMessage());
        }
    }
}
