package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO{

    @Override
    public AuthData addAuth(AuthData auth) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuthByAuthToken(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData deleteAuthByAuthToken(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void clearAuths() throws DataAccessException {

    }
}
