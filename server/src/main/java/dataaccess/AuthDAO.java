package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void addAuth(AuthData auth) throws DataAccessException;
    AuthData getAuthByUsername(String username) throws DataAccessException;
    //not sure if an updateAuth method will be necessary yet
    void deleteAuthByUsername(String username) throws DataAccessException;
    //clearAll method?
}
