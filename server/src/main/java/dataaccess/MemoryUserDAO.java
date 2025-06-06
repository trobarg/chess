package dataaccess;

import model.UserData;

import java.util.HashMap;
public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData addUser(UserData user) {
        return users.put(user.username(), user);
    } //doesn't hash password

    @Override
    public UserData getUserByUsername(String username) {
        return users.get(username);
    }

    @Override
    public void clearUsers() {
        users.clear();
    }
}
