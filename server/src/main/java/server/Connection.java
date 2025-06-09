package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class Connection {
    private String playerUsername;
    private Session session;

    public Connection (String playerUsername, Session session) {
        this.playerUsername = playerUsername;
        this.session = session;
    }

    public void send(ServerMessage serverMessage) throws IOException {
        session.getRemote().sendString(new Gson().toJson(serverMessage)); //not getAsyncRemote?
    }
    //not sure all these getters and setters will be necessary
    public String getPlayerUsername() {
        return playerUsername;
    }

    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
