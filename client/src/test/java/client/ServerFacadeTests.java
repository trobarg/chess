package client;

import exception.ResponseException;
import model.AuthData;
import model.CreateGameResult;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() throws ResponseException {
        AuthData response = serverFacade.register("testUser1", "password1", "testUser1@mail.com");
        assertNotNull(response);
        assertEquals("testUser1", response.username());
    }

    @Test
    public void registerDuplicateUsername() throws ResponseException {
        serverFacade.register("testUserDuplicate", "password", "duplicate@mail.com");
        assertThrows(ResponseException.class, () -> serverFacade.register("testUserDuplicate", "password", "duplicate@mail.com"));
    }

    @Test
    public void loginSuccess() throws ResponseException {
        serverFacade.register("testUser2", "password2", "testUser2@mail.com");
        AuthData response = serverFacade.login("testUser2", "password2");
        assertNotNull(response);
        assertEquals("testUser2", response.username());
    }

    @Test
    public void loginBadPassword() throws ResponseException {
        serverFacade.register("correctUser", "correctPassword", "wrong@mail.com");
        assertThrows(ResponseException.class, () -> serverFacade.login("correctUser", "wrongPassword"));
    }

    @Test
    public void logoutSuccess() throws ResponseException {
        serverFacade.register("testUser3", "password3", "testUser3@mail.com");
        serverFacade.logout();
        assertThrows(ResponseException.class, () -> serverFacade.listGames());
    }

    @Test
    public void logoutWhileLoggedOut () throws ResponseException {
        serverFacade.register("userToLogOut", "password", "logout@mail.com");
        serverFacade.logout();
        assertThrows(ResponseException.class, () -> serverFacade.logout());
    }

    @Test
    public void listGamesSuccess() throws ResponseException {
        serverFacade.register("testUser4", "password4", "testUser4@mail.com");
        serverFacade.createGame("testGame1");
        ArrayList<GameData> response = (ArrayList<GameData>) serverFacade.listGames();
        assertNotNull(response);
        ArrayList<String> responseNames = new ArrayList<>();
        for (GameData game : response) {
            responseNames.add(game.gameName());
        }
        assertTrue(responseNames.contains("testGame1"));
    }

    @Test
    public void listGamesWhileLoggedOut() throws ResponseException {
        serverFacade.register("userToList","password","lister@mail.com");
        assertThrows(ResponseException.class, () -> {
            serverFacade.logout();
            serverFacade.listGames();
        });
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        serverFacade.register("testUser5", "password5", "testUser5@mail.com");
        CreateGameResult response = serverFacade.createGame("testGame2");
        assertNotNull(response);
        assertTrue(response.gameID() >= 0);
    }

    @Test
    public void createGameWhileLoggedOut() throws ResponseException {
        serverFacade.register("userToGame", "password", "gamer@mail.com");
        assertThrows(ResponseException.class, () -> {
            serverFacade.logout();
            serverFacade.createGame("shouldFailGame");
        });
    }

    @Test
    public void joinGameSuccess() throws ResponseException {
        serverFacade.register("testUser6", "password6", "testUser6@mail.com");
        serverFacade.createGame("testGame3");
        ArrayList<GameData> games = (ArrayList<GameData>) serverFacade.listGames();
        int i = 1;
        for (GameData game : games) {
            if (game.gameName() != null && game.gameName().equals("testGame3")) {
                serverFacade.joinGame(i, "WHITE");
            }
            else {
                i++;
            }
        }
        games = (ArrayList<GameData>) serverFacade.listGames();
        ArrayList<String> whiteUsernames = new ArrayList<>();
        for (GameData game : games) {
            whiteUsernames.add(game.whiteUsername());
        }
        assertTrue(whiteUsernames.contains("testUser6"));
    }

    @Test
    public void joinGameInvalidColor() throws ResponseException {
        serverFacade.register("purpleUser", "purplePassword", "purple@mail.com");
        serverFacade.createGame("badColorGame");
        ArrayList<GameData> games = (ArrayList<GameData>) serverFacade.listGames();
        int i = 1;
        for (GameData game : games) {
            if (game.gameName() != null && game.gameName().equals("badColorGame")) {
                int finalI = i;
                assertThrows(ResponseException.class, () -> serverFacade.joinGame(finalI, "PURPLE"));
            }
            else {
                i++;
            }
        }
    }
}
