package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{
    public SQLAuthDAO() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException exception) {
            throw new DataAccessException("Failed to create database", exception);
        }
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS auths " +
                    "(authToken VARCHAR(255) PRIMARY KEY, username VARCHAR(255))")) {
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Failed to create auths table", exception);
        }
    }

    @Override
    public AuthData addAuth(AuthData auth) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("INSERT INTO auths (authToken, username) VALUES (?, ?)")) {
                AuthData previousAuth = getAuthByAuthToken(auth.authToken());
                statement.setString(1, auth.authToken());
                statement.setString(2, auth.username());
                statement.executeUpdate();
                return previousAuth;
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to add auth", exception);
        }
    }

    @Override
    public AuthData getAuthByAuthToken(String authToken) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("SELECT authToken, username FROM auths WHERE authToken = ?")) {
                statement.setString(1, authToken);
                try (var result = statement.executeQuery()) {
                    result.next();
                    var username = result.getString("username");
                    return new AuthData(authToken, username);
                }
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to get auth", exception);
        }
    }

    @Override
    public AuthData deleteAuthByAuthToken(String authToken) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("DELETE FROM auths WHERE authToken = ?")) {
                AuthData previousAuth = getAuthByAuthToken(authToken);
                statement.setString(1, authToken);
                statement.executeUpdate();
                return previousAuth;
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to delete auth", exception);
        }
    }

    @Override
    public void clearAuths() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("TRUNCATE auths")) {
                statement.executeUpdate();
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to clear auths", exception);
        }
    }
}
