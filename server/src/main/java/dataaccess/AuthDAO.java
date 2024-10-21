package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void addAuth(AuthData auth) throws DataAccessException;
    AuthData getAuthByAuthToken(String authToken) throws DataAccessException;
    //not sure if an updateAuth method will be necessary yet
    void deleteAuthByAuthToken(String authToken) throws DataAccessException;
    void clearAuths();
}
