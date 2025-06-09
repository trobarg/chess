package server;

import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(AuthData authData, Session session) {
        var connection = new Connection(authData.username(), session);
        connections.put(authData.authToken(), connection);
    }
    public Connection getConnection(String authToken) {//could replace with a sendOnConnection if that's all it will be used for
        return connections.get(authToken);
    }
    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String excludedAuthToken, Notification notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.getSession().isOpen()) {
                if (!c.getPlayerUsername().equals(excludedAuthToken)) {
                    c.send(notification);
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c.getPlayerUsername());
        }
    }
}
