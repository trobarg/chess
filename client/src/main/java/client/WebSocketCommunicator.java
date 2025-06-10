package client;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.messages.Error;//so the compiler doesn't complain about naming collision with java.lang.Error
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {
    private final String url;
    Session session;
    ServerMessageHandler notificationHandler;

    public WebSocketCommunicator(String urlExtension, ServerMessageHandler notificationHandler) {
        url = "ws://" + urlExtension + "/ws";
        this.notificationHandler = notificationHandler;
    }

    public void establishConnection() throws ResponseException {
        try {
            URI socketURI = new URI(url);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case ERROR -> notificationHandler.notify(new Gson().fromJson(message, Error.class));
                        case NOTIFICATION -> notificationHandler.notify(new Gson().fromJson(message, Notification.class));
                        case LOAD_GAME -> notificationHandler.notify(new Gson().fromJson(message, LoadGame.class));
                    }
                }
            });
        }
        catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, "Failed to connect WebSocket: " + ex.getMessage());
        }

    }

    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message); //not getBasicRemote()?
    }

    public void closeConnection() throws ResponseException {
        if (this.session != null && this.session.isOpen()) {
            try {
                this.session.close();
            }
            catch (IOException exception) {
                throw new ResponseException(500, "Failed to close WebSocket: " + exception.getMessage());
            }
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
