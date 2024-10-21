package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> authorizations = new HashMap<>(); //user can have multiple authorizations

    @Override
    public void addAuth(AuthData auth) throws DataAccessException {
        String authToken = auth.authToken();
        if (authorizations.containsKey(authToken)) {
            throw new DataAccessException("Authorization already exists");
        }
        authorizations.put(authToken, auth);
    }

    @Override
    public AuthData getAuthByAuthToken(String authToken) throws DataAccessException {
        if (authorizations.containsKey(authToken)) {
            return authorizations.get(authToken);
        }
        throw new DataAccessException("Authorization does not exist");
    }

    @Override
    public void deleteAuthByAuthToken(String authToken) throws DataAccessException {
        if (authorizations.containsKey(authToken)) {
            authorizations.remove(authToken);
        }
        throw new DataAccessException("Authorization does not exist");
    }

    @Override
    public void clearAuths() {
        authorizations.clear();
    }

}
