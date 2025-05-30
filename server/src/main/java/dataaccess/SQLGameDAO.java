package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    public SQLGameDAO() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Failed to create database", exception);
        }
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS games (" +
                            "gameID INT PRIMARY KEY, " +
                            "whiteUsername VARCHAR(255), " +
                            "blackUsername VARCHAR(255), " +
                            "gameName VARCHAR(255), " +
                            "chessGame TEXT)")) {
                statement.executeUpdate();
            }
        } catch (SQLException | DataAccessException exception) {
            throw new RuntimeException("Failed to create games table", exception);
        }
    }

    @Override
    public GameData addGame(GameData game) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("INSERT INTO games " +
                    "(gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES(?, ?, ?, ?, ?)")) {
                GameData previousGameData = getGameByID(game.gameID());
                statement.setInt(1, game.gameID());
                statement.setString(2, game.whiteUsername());
                statement.setString(3, game.blackUsername());
                statement.setString(4, game.gameName());
                statement.setString(5, serializeGame(game.game()));
                statement.executeUpdate();
                return previousGameData;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private String serializeGame(ChessGame game) {
        return new Gson().toJson(game);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        HashSet<GameData> retGames = new HashSet<>(); //maybe SQL table should be called game, not games?
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("SELECT * FROM games")) {
                try (var results = statement.executeQuery()) {
                    while (results.next()) {
                        var gameID = results.getInt("gameID"); //nesting depth issue?
                        var whiteUsername = results.getString("whiteUsername");
                        var blackUsername = results.getString("blackUsername");
                        var gameName = results.getString("gameName");
                        var chessGame = deserializeGame(results.getString("chessGame"));
                        retGames.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Failed to list games", exception);
        }
        return retGames;
    }

    private ChessGame deserializeGame(String serializedGame) {
        return new Gson().fromJson(serializedGame, ChessGame.class);
    }

    @Override
    public Collection<Integer> getGameIDs() throws DataAccessException {
        HashSet<Integer> gameIDs = new HashSet<>();
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("SELECT gameID FROM games")) {//not sure that this works like I want
                try (var results = statement.executeQuery()) {
                    while (results.next()) {
                        gameIDs.add(results.getInt("gameID"));
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Failed to get game IDs", exception);
        }
        return gameIDs;
    }

    @Override
    public GameData getGameByID(int gameID) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("SELECT whiteUsername, blackUsername, gameName, chessGame FROM games WHERE gameID=?")) {
                statement.setInt(1, gameID);
                try (var results = statement.executeQuery()) {
                    if (!results.next()) {
                        return null;
                    }
                    var whiteUsername = results.getString("whiteUsername");
                    var blackUsername = results.getString("blackUsername");
                    var gameName = results.getString("gameName");
                    var chessGame = deserializeGame(results.getString("chessGame"));
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Failed to get game", exception);
        }
    }

    @Override
    public GameData updateGame(GameData game) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("UPDATE games SET " +
                    "whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameID=?")) {
                GameData previousGameData = getGameByID(game.gameID());
                statement.setString(1, game.whiteUsername());
                statement.setString(2, game.blackUsername());
                statement.setString(3, game.gameName());
                statement.setString(4, serializeGame(game.game()));
                statement.setInt(5, game.gameID());
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new DataAccessException("Game requested to be updated not found in database.");
                }
                return previousGameData;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Failed to update game", exception);
        }
    }

    @Override
    public void clearGames() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.prepareStatement("TRUNCATE games")) {
                statement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Failed to clear games", exception);
        }
    }
}
