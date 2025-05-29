package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{
    public SQLUserDAO() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Failed to create database", exception);
        }
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS users " +
                    "(username VARCHAR(255) PRIMARY KEY, password VARCHAR(255), email VARCHAR(255))")) {
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException exception) {
            throw new RuntimeException("Failed to create users table", exception);
        }
    }

    @Override
    public UserData addUser(UserData user) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("INSERT INTO users (username, password, email) VALUES (?, ?, ?)")) {
                UserData previousUserData = getUserByUsername(user.username());
                statement.setString(1, user.username());
                statement.setString(2, hashPassword(user.password()));
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
                    result.next();
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

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
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
