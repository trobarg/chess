package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void registerSuccess() throws ResponseException {
        AuthData result = userService.register(new UserData("alice", "password123", "alice@example.com"));

        assertEquals("alice", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void registerUsernameTaken() {
        UserData user = new UserData("alice", "password123", "alice@example.com");
        userDAO.addUser(user);

        ResponseException ex = assertThrows(ResponseException.class, () -> userService.register(user));

        assertEquals(403, ex.statusCode());
        assertEquals("Error: Username is already in use", ex.getMessage());
    }

    @Test
    void loginSuccess() throws ResponseException {
        userDAO.addUser(new UserData("bob", "secretpassword", "bob@example.com"));

        AuthData result = userService.login(new LoginRequest("bob", "secretpassword"));

        assertEquals("bob", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginWrongPassword() {
        userDAO.addUser(new UserData("bob", "correctpassword", "bob@example.com"));

        LoginRequest request = new LoginRequest("bob", "wrongpassword");

        ResponseException ex = assertThrows(ResponseException.class, () -> userService.login(request));

        assertEquals(401, ex.statusCode());
        assertEquals("Error: Unauthorized", ex.getMessage());
    }

    @Test
    void loginUserNotFound() {
        LoginRequest request = new LoginRequest("nonexistent", "password");

        ResponseException ex = assertThrows(ResponseException.class, () -> userService.login(request));

        assertEquals(401, ex.statusCode());
        assertEquals("Error: Unauthorized", ex.getMessage());
    }

    @Test
    void logoutSuccess() throws ResponseException {
        String token = UUID.randomUUID().toString();
        authDAO.addAuth(new AuthData(token, "carol"));

        userService.logout(new RequestWithAuth(token));

        assert(authDAO.getAuthByAuthToken(token) == null);
    }

    @Test
    void logoutInvalidToken() {
        RequestWithAuth request = new RequestWithAuth("invalid-token");

        ResponseException ex = assertThrows(ResponseException.class, () -> userService.logout(request));

        assertEquals(401, ex.statusCode());
        assertEquals("Error: Unauthorized", ex.getMessage());
    }

}


