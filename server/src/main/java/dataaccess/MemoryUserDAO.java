package dataaccess;

import model.UserData;

public class MemoryUserDAO implements UserDAO {
    @Override
    public void addUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUserByUsername(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteUserByUsername(String username) throws DataAccessException {

    }
}
