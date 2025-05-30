package dataaccess;
import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTests {
    private SQLGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new SQLGameDAO();
        gameDAO.clearGames();
    }

    @Test
    public void addGameSuccess() throws DataAccessException {
        gameDAO.addGame(new GameData(1, "whiteUser", "blackUser", "Game One", new ChessGame()));

        GameData retrieved = gameDAO.getGameByID(1);
        assertNotNull(retrieved);
        assertEquals("whiteUser", retrieved.whiteUsername());
        assertEquals("Game One", retrieved.gameName());
    }

    @Test
    public void addGameDuplicateID() throws DataAccessException {
        gameDAO.addGame(new GameData(2, "w", "b", "Original", new ChessGame()));
        GameData duplicate = new GameData(2, "w2", "b2", "Duplicate", new ChessGame());

        assertThrows(DataAccessException.class, () -> gameDAO.addGame(duplicate));
    }

    @Test
    public void getGameByIDSuccess() throws DataAccessException {
        gameDAO.addGame(new GameData(3, "white", "black", "Test Game", new ChessGame()));

        GameData retrieved = gameDAO.getGameByID(3);
        assertNotNull(retrieved);
        assertEquals("Test Game", retrieved.gameName());
    }

    @Test
    public void getGameByIDNonexistent() throws DataAccessException {
        GameData retrieved = gameDAO.getGameByID(999);
        assertNull(retrieved);
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        gameDAO.addGame(new GameData(4, "w1", "b1", "Game A", new ChessGame()));
        gameDAO.addGame(new GameData(5, "w2", "b2", "Game B", new ChessGame()));

        Collection<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void listGamesEmptyTable() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        gameDAO.addGame(new GameData(6, "w", "b", "Old Name", new ChessGame()));

        GameData updated = new GameData(6, "w", "b", "Updated Name", new ChessGame());
        gameDAO.updateGame(updated);

        GameData retrieved = gameDAO.getGameByID(6);
        assertEquals("Updated Name", retrieved.gameName());
    }

    @Test
    public void updateGameNonexistent() {
        GameData updated = new GameData(7, "w", "b", "Missing Game", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(updated));
    }

    @Test
    public void getGameIDsSuccess() throws DataAccessException {
        gameDAO.addGame(new GameData(8, "w", "b", "Game11", new ChessGame()));
        gameDAO.addGame(new GameData(9, "w", "b", "Game12", new ChessGame()));

        Collection<Integer> ids = gameDAO.getGameIDs();
        assertTrue(ids.contains(8));
        assertTrue(ids.contains(9));
    }

    @Test
    public void getGameIDsEmptyTable() throws DataAccessException {
        Collection<Integer> ids = gameDAO.getGameIDs();
        assertNotNull(ids);
        assertTrue(ids.isEmpty());
    }

    @Test
    public void clearGamesSuccess() throws DataAccessException {
        gameDAO.addGame(new GameData(10, "w", "b", "Clear This", new ChessGame()));
        assertNotNull(gameDAO.getGameByID(10));

        gameDAO.clearGames();

        assertNull(gameDAO.getGameByID(10));
    }
}
