package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO; //feels like there should be a WebSocketService layer in between


    public WebSocketHandler (UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(session, new Gson().fromJson(message, Connect.class));
            case LEAVE -> leave(session, new Gson().fromJson(message, Leave.class));
            case MAKE_MOVE -> makeMove(session, new Gson().fromJson(message, MakeMove.class));
            case RESIGN -> resign(session, new Gson().fromJson(message, Resign.class));
        }
    }
    private void connect(Session session, Connect connect) throws IOException {
        try {
            AuthData authData = authDAO.getAuthByAuthToken(connect.getAuthToken());
            GameData game = gameDAO.getGameByID(connect.getGameID());
            connectionManager.add(authData, session);
            ChessGame.TeamColor connectColor = connect.getPlayerColor();
            boolean usernameMatch;
            if (connectColor == ChessGame.TeamColor.WHITE) {
                usernameMatch = Objects.equals(game.whiteUsername(), authData.username());
            }
            else {
                usernameMatch = Objects.equals(game.blackUsername(), authData.username());
            }
            if (!usernameMatch) {
                connectionManager.getConnection(authData.authToken()).send(new Error(
                        "Error: attempted to connect as color not playing as"));
            }
            Notification notification = new Notification("%s has joined the game as %s".formatted(authData.username(), connectColor.toString()));
            connectionManager.broadcast(authData.authToken(), notification);
            LoadGame loadGame = new LoadGame(game.game());
            connectionManager.getConnection(authData.authToken()).send(loadGame);
        }
        catch (DataAccessException exception) {

        }
    }
    private void leave(Session session, Leave leave) {

        //Username needed for the message, even if broadcast by AuthToken
    }
    private void makeMove(Session session, MakeMove makeMove) {

    }
    private void resign(Session session, Resign resign) {

    }


}
