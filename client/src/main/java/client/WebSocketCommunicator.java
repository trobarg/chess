package client;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.messages.Notification;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {
    private final String url;
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketCommunicator(String urlExtension, NotificationHandler notificationHandler) {
        url = "ws://" + urlExtension;
        this.notificationHandler = notificationHandler;
    }

    public void establishConnection() throws ResponseException {
        try {
            URI socketURI = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Notification notification = new Gson().fromJson(message, Notification.class); //not sure this will work for all message types
                    notificationHandler.notify(notification);
                }
            });
        }
        catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }

    }

    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message); //not getBasicRemote()
        //Will some commands require closing the session?
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
