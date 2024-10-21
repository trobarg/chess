package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void addUser(UserData user) throws DataAccessException {
        String username = user.username();
        if (users.containsKey(username)) {
            throw new DataAccessException("Username already exists"); //correct approach?
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUserByUsername(String username) throws DataAccessException {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        throw new DataAccessException("Username not found"); //or simply return null?
    }

    @Override
    public void deleteUserByUsername(String username) throws DataAccessException {
        if (users.containsKey(username)) { //condition not strictly necessary
            users.remove(username);
        }
        throw new DataAccessException("Username not found");
    }

    @Override
    public void clearUsers() {
        users.clear();
    }
}
