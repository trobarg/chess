package client;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    //endpoint calling not yet implemented
    public boolean register(String username, String password, String email) {
        return true;
    }
    public boolean login(String username, String password) {
        return true;
    }
}
