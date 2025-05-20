package service;
import java.util.UUID;

import dataaccess.*;
import model.*;

public class UserService {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    //What type of constructor is needed?
    public AuthData register(UserData user) throws ResponseException {
        try {
            if (userDAO.getUserByUsername(user.username()) != null) {
                throw new ResponseException(403, "Error: Username is already in use");
            }
            else {
                userDAO.addUser(user);
                String authToken = UUID.randomUUID().toString();
                authDAO.addAuth(new AuthData(user.username(), authToken));
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
                throw new ResponseException(401, "Error: Unauthorized"); //Not some kind of username not found error?
            }
            else if (!userDAO.getUserByUsername(loginRequest.username()).password().equals(loginRequest.password())) {
                throw new ResponseException(401, "Error: Unauthorized");
            }
            else {
                String authToken = UUID.randomUUID().toString();
                authDAO.addAuth(new AuthData(loginRequest.username(), authToken));
                return authDAO.getAuthByAuthToken(authToken);
            }
        }
        catch (DataAccessException dAE) {
            throw new ResponseException(500, dAE.getMessage());
        }
    }

    public void logout(RequestWithAuth logoutRequest) throws ResponseException {//null return type?
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
