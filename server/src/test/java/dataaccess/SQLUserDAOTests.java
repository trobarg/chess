package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTests {
    private SQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.clearUsers();
    }

    @Test
    public void addUserSuccess() throws DataAccessException {
        userDAO.addUser(new UserData("alice", "password123", "alice@example.com"));

        UserData retrieved = userDAO.getUserByUsername("alice");
        assertNotNull(retrieved);
        assertEquals("alice", retrieved.username());
        assertEquals("alice@example.com", retrieved.email());
    }

    @Test
    public void addUserDuplicateUsername() throws DataAccessException {
        userDAO.addUser(new UserData("bob", "secretpassword", "bob@example.com"));

        UserData userSameUsername = new UserData("bob", "differentpassword", "bob2@example.com");

        assertThrows(DataAccessException.class, () -> userDAO.addUser(userSameUsername));
    }

    @Test
    public void getUserByUsernameSuccess() throws DataAccessException { //same as addUserSuccess test
        userDAO.addUser(new UserData("carol", "12345", "carol@example.com"));

        UserData retrieved = userDAO.getUserByUsername("carol");
        assertNotNull(retrieved);
        assertEquals("carol", retrieved.username());
        assertEquals("carol@example.com", retrieved.email());
    }

    @Test
    public void getUserByUsernameNonexistent() throws DataAccessException {
        UserData retrieved = userDAO.getUserByUsername("nonexistent");
        assertNull(retrieved);
    }

    @Test
    public void clearUsersSuccess() throws DataAccessException {
        userDAO.addUser(new UserData("dave", "pass", "dave@example.com"));
        assertNotNull(userDAO.getUserByUsername("dave"));

        userDAO.clearUsers();

        assertNull(userDAO.getUserByUsername("dave"));
    }
}
