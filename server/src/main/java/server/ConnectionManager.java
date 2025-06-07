package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String playerUsername, Session session) {
        var connection = new Connection(playerUsername, session);
        connections.put(playerUsername, connection);
    }

    public void remove(String playerUsername) {
        connections.remove(playerUsername);
    }

    public void broadcast(String excludePlayerUsername, Notification notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.getSession().isOpen()) {
                if (!c.getPlayerUsername().equals(excludePlayerUsername)) {
                    c.send(notification.toString());
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
