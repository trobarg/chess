package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final ConcurrentHashMap<Session, Integer> sessions = new ConcurrentHashMap<>();

    public void add(Session session, int gameID) {
        sessions.put(session, gameID);
    }
    public void send(Session session, ServerMessage serverMessage) throws IOException {
        session.getRemote().sendString(new Gson().toJson(serverMessage)); //not getAsyncRemote? Also no isOpen?
    }
    public void remove(Session session) {
        sessions.remove(session);
    }
    public void broadcast(Session ownSession, ServerMessage message) throws IOException {
        broadcast(ownSession, message, false);
    }
    public void broadcast(Session ownSession, ServerMessage message, boolean includeOwn) throws IOException {
        var removeList = new ArrayList<Session>();
        int gameID = sessions.get(ownSession);
        for (Session session : sessions.keySet()) {
            if (session.isOpen()) {
                if (sessions.get(session).equals(gameID)) {
                    if (!session.equals(ownSession) || includeOwn) {
                        send(session, message);
                    }
                }
            }
            else {
                removeList.add(session);
            }
        }
        for (Session session : removeList) {
            sessions.remove(session);
        }
    }
}
