package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{

    @Override
    public UserData addUser(UserData user) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
                UserData previousUserData = getUserByUsername(user.username());
                statement.setString(1, user.username());
                statement.setString(2, user.password()); //hash password here?
                statement.setString(3, user.email());
                statement.executeUpdate();
                return previousUserData;
            }
        }
        catch (SQLException exception) { //not sure if it should be catching a DataAccessException instead
            throw new DataAccessException("Failed to add user", exception);
        }
    }

    @Override
    public UserData getUserByUsername(String username) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("SELECT username, password, email FROM users WHERE username = ?")) {
                statement.setString(1, username);
                try (var result = statement.executeQuery()) {
                    //need result.next() here?
                    var password = result.getString("password");
                    var email = result.getString("email");
                    return new UserData(username, password, email);
                }
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to get user", exception);
        }
    }

    @Override
    public void clearUsers() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("TRUNCATE users")) {
                statement.executeUpdate();
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to clear users", exception);
        }
    }
}
