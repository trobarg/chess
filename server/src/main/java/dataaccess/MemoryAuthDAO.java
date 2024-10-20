package dataaccess;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    @Override
    public void addAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getAuthByUsername(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuthByUsername(String username) throws DataAccessException {

    }
}
