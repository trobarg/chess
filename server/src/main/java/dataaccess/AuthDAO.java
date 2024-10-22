package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData addAuth(AuthData auth) throws DataAccessException;
    AuthData getAuthByAuthToken(String authToken) throws DataAccessException;
    //not sure if an updateAuth method will be necessary yet
    AuthData deleteAuthByAuthToken(String authToken) throws DataAccessException;
    void clearAuths() throws DataAccessException;
}
