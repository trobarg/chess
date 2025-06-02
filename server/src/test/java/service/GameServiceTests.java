package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private SQLGameDAO gameDAO;
    private SQLAuthDAO authDAO;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameDAO = new SQLGameDAO();
        authDAO = new SQLAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    void listGamesSuccess() throws ResponseException, DataAccessException {
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "user1"));
        Collection<GameData> result1 = gameService.listGames(new RequestWithAuth(token));

        gameDAO.addGame(new GameData(10, null, null, "Test Game", new ChessGame()));

        Collection<GameData> result2 = gameService.listGames(new RequestWithAuth(token));

        assertEquals(result1.size() + 1, result2.size());
    }

    @Test
    void listGamesUnauthorized() {
        RequestWithAuth request = new RequestWithAuth("bad-token");

        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.listGames(request));

        assertEquals(401, ex.statusCode());
        assertEquals("Error: Unauthorized", ex.getMessage());
    }

    @Test
    void createGameSuccess() throws ResponseException, DataAccessException {
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "creator"));

        CreateGameRequest request = new CreateGameRequest(token, "New Game");
        CreateGameResult result = gameService.createGame(request);

        assertNotNull(result);
        assertTrue(gameDAO.getGameIDs().contains(result.gameID()));
    }

    @Test
    void createGameUnauthorized() {
        CreateGameRequest request = new CreateGameRequest("bad-token", "Game");

        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.createGame(request));

        assertEquals(401, ex.statusCode());
        assertEquals("Error: Unauthorized", ex.getMessage());
    }

    @Test
    void joinGameWhiteSuccess() throws ResponseException, DataAccessException {
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "whitePlayer"));
        GameData game = new GameData(20, null, null, "Joinable Game", new ChessGame());
        gameDAO.addGame(game);

        JoinGameRequest request = new JoinGameRequest(token, "WHITE", 20);
        gameService.joinGame(request);

        assertEquals("whitePlayer", gameDAO.getGameByID(20).whiteUsername());
    }

    @Test
    void joinGameBlackSuccess() throws ResponseException, DataAccessException {
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "blackPlayer"));
        GameData game = new GameData(30, null, null, "Joinable Game", new ChessGame());
        gameDAO.addGame(game);

        JoinGameRequest request = new JoinGameRequest(token, "BLACK", 30);
        gameService.joinGame(request);

        assertEquals("blackPlayer", gameDAO.getGameByID(30).blackUsername());
    }

    @Test
    void joinGameAlreadyTaken() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "player"));
        GameData game = new GameData(40, "someone", null, "Taken Game", new ChessGame());
        gameDAO.addGame(game);

        JoinGameRequest request = new JoinGameRequest(token, "WHITE", 40);

        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.joinGame(request));

        assertEquals(403, ex.statusCode());
        assertEquals("Error: Already taken", ex.getMessage());
    }

    @Test
    void joinGameInvalidColor() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "player"));
        GameData game = new GameData(50, null, null, "Bad Color Game", new ChessGame());
        gameDAO.addGame(game);

        JoinGameRequest request = new JoinGameRequest(token, "BLUE", 4);

        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.joinGame(request));

        assertEquals(400, ex.statusCode());
        assertEquals("Error: Bad request", ex.getMessage());
    }

    @Test
    void joinGameUnauthorized() {
        JoinGameRequest request = new JoinGameRequest("bad-token", "WHITE", 5);

        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.joinGame(request));

        assertEquals(401, ex.statusCode());
        assertEquals("Error: Unauthorized", ex.getMessage());
    }
}
