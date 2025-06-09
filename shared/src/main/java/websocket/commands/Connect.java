package websocket.commands;

import chess.ChessGame;

public class Connect extends UserGameCommand {
    private final ChessGame.TeamColor playerColor;

    public Connect(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(CommandType.CONNECT, authToken, gameID);
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
