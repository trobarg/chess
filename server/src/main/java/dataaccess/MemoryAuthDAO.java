package dataaccess;

import model.AuthData;

import java.util.HashMap;

//throws DataAccessException on methods?
public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> authorizations = new HashMap<>(); //user can have multiple authorizations

    @Override
    public AuthData addAuth(AuthData auth) {
        return authorizations.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuthByAuthToken(String authToken) {
       return authorizations.get(authToken);
    }

    @Override
    public AuthData deleteAuthByAuthToken(String authToken) {
        return authorizations.remove(authToken);
    }

    @Override
    public void clearAuths() {
        authorizations.clear();
    }

}
