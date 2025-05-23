package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {

    private MemoryUserDAO userDAO;
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;
    private ClearService clearService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    void clearApplicationSuccess() throws DataAccessException {
        userDAO.addUser(new UserData("john", "pass123", "john@example.com"));
        gameDAO.addGame(new GameData(1, null, null, "TestGame", new ChessGame()));
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "john"));

        assertNotNull(userDAO.getUserByUsername("john"));
        assertFalse(gameDAO.listGames().isEmpty());
        assertNotNull(authDAO.getAuthByAuthToken(token));

        clearService.clearApplication(new ClearApplicationRequest());

        assertNull(userDAO.getUserByUsername("john"));
        assertTrue(gameDAO.listGames().isEmpty());
        assertNull(authDAO.getAuthByAuthToken(token));
    }
}
