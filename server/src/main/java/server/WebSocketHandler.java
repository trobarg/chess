package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.Error;//so the compiler doesn't complain about naming collision with java.lang.Error
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final SessionManager sessionManager = new SessionManager();
    private final GameDAO gameDAO;
    private final AuthDAO authDAO; //feels like there should be a WebSocketService layer in between


    public WebSocketHandler (GameDAO gameDAO, AuthDAO authDAO) {
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
    //Should every method have a check for valid authToken and gameID?
    private void connect(Session session, Connect connect) throws IOException {
        try {
            String authToken = connect.getAuthToken();
            AuthData authData = authDAO.getAuthByAuthToken(authToken);
            if (authData == null) {
                sessionManager.send(session, new Error("Not authorized to connect"));
                return;
            }
            String username = authData.username();
            int gameId = connect.getGameID();
            GameData gameData = gameDAO.getGameByID(gameId);
            if (gameData == null) {
                sessionManager.send(session, new Error("Invalid game ID provided"));
                return;
            }
            sessionManager.add(session, gameId);
            ChessGame.TeamColor connectColor = getPlayerColor(username, gameData);
            if (connectColor != null) {
                sessionManager.broadcast(session,
                        new Notification("%s has joined the game as %s".formatted(username, connectColor.toString())));
                if (getColorUsername(connectColor, gameData) == null) { //http join should be taking care of this
                    if (connectColor.equals(ChessGame.TeamColor.WHITE)) {
                        gameDAO.updateGame(new GameData(gameData.gameID(), username, gameData.blackUsername(),
                                gameData.gameName(), gameData.game()));
                    }
                    else {
                        gameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), username,
                                gameData.gameName(), gameData.game()));
                    }
                }
            }
            else {
                sessionManager.broadcast(session,
                        new Notification("%s has joined the game as an observer".formatted(username)));
            }
            LoadGame loadGame = new LoadGame(gameData.game());
            sessionManager.send(session, loadGame);
        }
        catch (DataAccessException exception) {
            //could be bad query for AuthData or GameData
            sessionManager.send(session, new Error(exception.getMessage()));
        }
    }
    private void leave(Session session, Leave leave) throws IOException {
        try {
            String authToken = leave.getAuthToken();
            String username = authDAO.getAuthByAuthToken(authToken).username();
            GameData gameData = gameDAO.getGameByID(leave.getGameID());
            ChessGame.TeamColor leaveColor = getPlayerColor(username, gameData);
            if (leaveColor != null) {
                sessionManager.broadcast(session,
                        new Notification("%s (playing %s) has left the game".formatted(username, leaveColor.toString())));
                if (leaveColor.equals(ChessGame.TeamColor.WHITE)) {
                    gameDAO.updateGame(new GameData(gameData.gameID(), null, gameData.blackUsername(),
                            gameData.gameName(), gameData.game()));
                }
                else {
                    gameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), null,
                            gameData.gameName(), gameData.game()));
                }
            }
            else {
                sessionManager.broadcast(session,
                        new Notification("%s (observer) has left the game".formatted(username)));
            }
            sessionManager.remove(session);
        }
        catch (DataAccessException exception) {
            sessionManager.send(session, new Error(exception.getMessage()));
        }
    }
    private void makeMove(Session session, MakeMove makeMove) throws IOException {
        try {
            String authToken = makeMove.getAuthToken();
            AuthData authData = authDAO.getAuthByAuthToken(authToken);
            if (authData == null) {
                sessionManager.send(session, new Error("Not authorized to move"));
                return;
            }
            String username = authData.username();
            GameData gameData = gameDAO.getGameByID(makeMove.getGameID());
            ChessGame game = gameData.game();
            ChessGame.TeamColor moveColor = getPlayerColor(username, gameData);
            if (moveColor == null) {
                sessionManager.send(session, new Error("You are observing this game"));
                return;
            }
            if (game.getGameOver()) {
                sessionManager.send(session, new Error("This game is over"));
                return;
            }
            if (game.getTeamTurn().equals(moveColor)) {
                try {
                    game.makeMove(makeMove.getMove());
                    ChessGame.TeamColor opponentColor = moveColor ==
                            ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
                    ChessPosition start = makeMove.getMove().getStartPosition();
                    ChessPosition end = makeMove.getMove().getEndPosition();
                    sessionManager.broadcast(session, new Notification(toChessNotation(start) + " to " + toChessNotation(end)));
                        if (game.isInCheckmate(opponentColor)) {
                            sessionManager.broadcast(session, new Notification("Checkmate! %s wins!".formatted(username)), true);
                        }
                        else if (game.isInStalemate(opponentColor)) {
                            sessionManager.broadcast(session, new Notification("Stalemate! It's a tie!"), true);
                        }
                        else if (game.isInCheck(opponentColor)) {
                            sessionManager.broadcast(session, new Notification("%s is in check".formatted(opponentColor.toString())), true);
                        }
                        sessionManager.broadcast(session, new LoadGame(game), true);
                        gameDAO.updateGame(gameData);
                }
                catch (InvalidMoveException exception) {
                    sessionManager.send(session, new Error("Illegal move"));
                }
            }
            else {
                sessionManager.send(session, new Error("It is not your turn"));
            }
        }
        catch (DataAccessException exception) {
            sessionManager.send(session, new Error(exception.getMessage()));
        }
    }
    private void resign(Session session, Resign resign) throws IOException {
        try {
            String authToken = resign.getAuthToken();
            String username = authDAO.getAuthByAuthToken(authToken).username();
            GameData gameData = gameDAO.getGameByID(resign.getGameID());
            ChessGame game = gameData.game();
            ChessGame.TeamColor resignColor = getPlayerColor(username, gameData);
            if (resignColor == null) {
                sessionManager.send(session, new Error("You are observing this game"));
                return;
            }
            if (game.getGameOver()) {
                sessionManager.send(session, new Error("This game is over"));
                return;
            }
            String opponentUsername = resignColor == ChessGame.TeamColor.WHITE ? gameData.blackUsername() : gameData.whiteUsername();
            game.setGameOver(true);
            gameDAO.updateGame(gameData);
            sessionManager.broadcast(session, new Notification("%s forfeits! %s wins!".formatted(username, opponentUsername)), true);
        }
        catch (DataAccessException exception) {
            sessionManager.send(session, new Error(exception.getMessage()));
        }
    }
    private ChessGame.TeamColor getPlayerColor(String username, GameData gameData) {
        if (username.equals(gameData.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        else if (username.equals(gameData.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        else {
            return null;
        }
    }
    private String getColorUsername (ChessGame.TeamColor color, GameData gameData) {
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            return gameData.whiteUsername();
        }
        else if (color.equals(ChessGame.TeamColor.BLACK)) {
            return gameData.blackUsername();
        }
        else {
            return null;
        }
    }
    private String toChessNotation(ChessPosition position) {
        char columnLetter = (char) ('h' - (position.getColumn()));
        return "" + columnLetter + position.getRow();
    }

}
