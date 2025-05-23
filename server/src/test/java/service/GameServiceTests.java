package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    void listGamesSuccess() throws ResponseException {
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "user1"));
        gameDAO.addGame(new GameData(1, null, null, "Test Game", new ChessGame()));

        Collection<GameData> result = gameService.listGames(new RequestWithAuth(token));

        assertEquals(1, result.size());
    }

    @Test
    void listGamesUnauthorized() {
        RequestWithAuth request = new RequestWithAuth("bad-token");

        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.listGames(request));

        assertEquals(401, ex.statusCode());
        assertEquals("Error: Unauthorized", ex.getMessage());
    }

    @Test
    void createGameSuccess() throws ResponseException {
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
    void joinGameWhiteSuccess() throws ResponseException {
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "whitePlayer"));
        GameData game = new GameData(1, null, null, "Joinable Game", new ChessGame());
        gameDAO.addGame(game);

        JoinGameRequest request = new JoinGameRequest(token, "WHITE", 1);
        gameService.joinGame(request);

        assertEquals("whitePlayer", gameDAO.getGameByID(1).whiteUsername());
    }

    @Test
    void joinGameBlackSuccess() throws ResponseException {
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "blackPlayer"));
        GameData game = new GameData(2, null, null, "Joinable Game", new ChessGame());
        gameDAO.addGame(game);

        JoinGameRequest request = new JoinGameRequest(token, "BLACK", 2);
        gameService.joinGame(request);

        assertEquals("blackPlayer", gameDAO.getGameByID(2).blackUsername());
    }

    @Test
    void joinGameAlreadyTaken() {
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "player"));
        GameData game = new GameData(3, "someone", null, "Taken Game", new ChessGame());
        gameDAO.addGame(game);

        JoinGameRequest request = new JoinGameRequest(token, "WHITE", 3);

        ResponseException ex = assertThrows(ResponseException.class, () -> gameService.joinGame(request));

        assertEquals(403, ex.statusCode());
        assertEquals("Error: Already taken", ex.getMessage());
    }

    @Test
    void joinGameInvalidColor() {
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "player"));
        GameData game = new GameData(4, null, null, "Bad Color Game", new ChessGame());
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
