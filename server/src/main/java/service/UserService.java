package service;

import java.util.UUID;

import dataaccess.*;
import service.ServiceException; //necessary when in the same package?
import model.UserData;
import model.AuthData;

public class UserService {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

    public AuthData register(UserData user) throws ServiceException {
        try {
            if (userDAO.getUserByUsername(user.username()) != null) {
                throw new ServiceException(403, "Error: Username is already in use");
            }
            else {
                userDAO.addUser(user);
                String authToken = UUID.randomUUID().toString();
                authDAO.addAuth(new AuthData(user.username(), authToken));
                return authDAO.getAuthByAuthToken(authToken);
            }
        }
        catch (DataAccessException dAE) {
            throw new ServiceException(500, dAE.getMessage());
        }
    }

    public AuthData login(UserData user) throws ServiceException { //login won't contain email
        try {
            if (userDAO.getUserByUsername(user.username()) == null) {
                throw new ServiceException(401, "Error: Unauthorized"); //Not some kind of username not found error?
            }
            else if (!userDAO.getUserByUsername(user.username()).password().equals(user.password())) {
                throw new ServiceException(401, "Error: Unauthorized");
            }
            else {
                String authToken = UUID.randomUUID().toString();
                authDAO.addAuth(new AuthData(user.username(), authToken));
                return authDAO.getAuthByAuthToken(authToken);
            }
        }
        catch (DataAccessException dAE) {
            throw new ServiceException(500, dAE.getMessage());
        }
    }

    public void logout(AuthData auth) throws ServiceException {
        try {
            if (authDAO.getAuthByAuthToken(auth.authToken()) == null) {
                throw new ServiceException(401, "Error: Unauthorized");
            }
            else {
                authDAO.deleteAuthByAuthToken(auth.authToken());
            }
        }
        catch (DataAccessException dAE) {
            throw new ServiceException(500, dAE.getMessage());
        }
    }
}
