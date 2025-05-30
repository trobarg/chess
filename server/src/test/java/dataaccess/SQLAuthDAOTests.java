package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTests {
    private SQLAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new SQLAuthDAO();
        authDAO.clearAuths();
    }

    @Test
    public void addAuthSuccess() throws DataAccessException {
        authDAO.addAuth(new AuthData("token123", "userA"));

        AuthData retrieved = authDAO.getAuthByAuthToken("token123");
        assertNotNull(retrieved);
        assertEquals("userA", retrieved.username());
    }

    @Test
    public void addAuthDuplicateToken() throws DataAccessException {
        authDAO.addAuth(new AuthData("tokenABC", "user1"));

        AuthData duplicate = new AuthData("tokenABC", "user2");

        assertThrows(DataAccessException.class, () -> authDAO.addAuth(duplicate));
    }

    @Test
    public void getAuthByAuthTokenSuccess() throws DataAccessException {
        authDAO.addAuth(new AuthData("tokenGet", "userGet"));

        AuthData retrieved = authDAO.getAuthByAuthToken("tokenGet");
        assertNotNull(retrieved);
        assertEquals("userGet", retrieved.username());
    }

    @Test
    public void getAuthByAuthTokenNonexistent() throws DataAccessException {
        AuthData retrieved = authDAO.getAuthByAuthToken("nonexistentToken");
        assertNull(retrieved);
    }

    @Test
    public void deleteAuthByAuthTokenSuccess() throws DataAccessException {
        authDAO.addAuth(new AuthData("tokenDel", "userDel"));

        AuthData deleted = authDAO.deleteAuthByAuthToken("tokenDel");
        assertNotNull(deleted);
        assertEquals("userDel", deleted.username());

        AuthData afterDelete = authDAO.getAuthByAuthToken("tokenDel");
        assertNull(afterDelete);
    }

    @Test
    public void deleteAuthByAuthTokenNonexistent() throws DataAccessException {
        AuthData deleted = authDAO.deleteAuthByAuthToken("missingToken");
        assertNull(deleted);
    }

    @Test
    public void clearAuthsSuccess() throws DataAccessException {
        authDAO.addAuth(new AuthData("tokenClear", "userClear"));
        assertNotNull(authDAO.getAuthByAuthToken("tokenClear"));

        authDAO.clearAuths();

        assertNull(authDAO.getAuthByAuthToken("tokenClear"));
    }
}

