package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData addUser(UserData user) throws DataAccessException;
    UserData getUserByUsername(String username) throws DataAccessException;
    //not sure if an updateUser method will be necessary yet
    UserData deleteUserByUsername(String username) throws DataAccessException;
    void clearUsers() throws DataAccessException;
}
