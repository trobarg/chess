package client;

import chess.ChessGame;
import exception.ResponseException;
import ui.BoardPrinter;

import java.util.Arrays;

public class GameplayClient implements Client {
    private int changeClientLayer = 0;
    private final ChessGame game; //not strictly certain this is necessary
    private final ChessGame.TeamColor teamColor;
    private final BoardPrinter boardPrinter;
    private final ServerFacade server;

    public GameplayClient(ServerFacade server, int gameNumber, ChessGame.TeamColor teamColor) throws ResponseException {
        this.server = server;
        this.teamColor = teamColor;
        this.game = server.getGameDataByNumber(gameNumber).game();
        boardPrinter = new BoardPrinter(this.game);
        boardPrinter.printBoard(this.teamColor, null);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var command = (tokens.length > 0) ? tokens[0] : "help";
            var parameters = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (command) {
                case "leave" -> leave();
                case "quit" -> "quit";
                default -> help();
            };
        }
        catch (Exception exception) {
            return exception.getMessage();
        }
    }

    private String leave() {
        changeClientLayer = -1;
        return "Successfully left game!";
    }

    private String help() {
        return """
                leave - leave the current game
                quit - quit the program
                help - display this message
               """;
    }

    public int getChangeClientLayer() {
        return changeClientLayer;
    }

    public void resetChangeClientLayer() {
        changeClientLayer = 0;
    }
}
