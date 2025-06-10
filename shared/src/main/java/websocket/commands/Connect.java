package websocket.commands;

import chess.ChessGame;

public class Connect extends UserGameCommand {

    public Connect(String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(CommandType.CONNECT, authToken, gameID);
    }

}
