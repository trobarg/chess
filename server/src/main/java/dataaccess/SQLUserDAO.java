package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO{

    @Override
    public UserData addUser(UserData user) throws DataAccessException {
        return null;
    }

    @Override
    public UserData getUserByUsername(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUsers() throws DataAccessException {

    }
}
