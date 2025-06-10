package client;

import chess.*;
import exception.ResponseException;
import ui.BoardPrinter;

import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.out;

public class GameplayClient implements Client {
    private int changeClientLayer = 0;
    private final int gameID;
    private final ChessGame.TeamColor teamColor;
    private final BoardPrinter boardPrinter;
    private final ServerFacade server;

    public GameplayClient(ServerFacade server, int gameNumber, ChessGame.TeamColor teamColor) throws ResponseException {
        this.server = server;
        this.teamColor = teamColor;
        this.gameID = server.getGameDataByNumber(gameNumber).gameID();
        boardPrinter = new BoardPrinter(server.getGameDataByNumber(gameNumber).game()); //game data should come from a LoadGame command?
        boardPrinter.printBoard(this.teamColor, null); //shouldn't need this line after LoadGame messages are working
    }

    public void updateAndPrintBoard(ChessGame updatedGame) {
        boardPrinter.updateGame(updatedGame);
        boardPrinter.printBoard(teamColor, null);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var command = (tokens.length > 0) ? tokens[0] : "help";
            var parameters = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (command) { //observer shouldn't be able to use all of these commands
                case "redraw" -> redraw();
                case "move" -> move(parameters);
                case "highlight" -> highlight(parameters);
                case "resign" -> resign();
                case "leave" -> leave();
                case "quit" -> "quit";
                default -> help();
            };
        }
        catch (Exception exception) {
            return exception.getMessage();
        }
    }

    private String redraw() {
        boardPrinter.printBoard(teamColor, null);
        return ""; //adherence to the eval() pattern is really at its limits here
    }

    private String move(String[] parameters) {
        if (parameters.length > 3 || parameters.length < 2) {
            return "Please provide 2 or 3 parameters: start position, end position, and promotion type if applicable";
            //Shouldn't promotion be required when applicable?
        }
        else if (!(parameters[0].matches("[a-h][1-8]") && parameters[1].matches("[a-h][1-8]"))) {
            return "Please provide positions in lowercase-letter number pairs. Ex: d4 e5";
        }

        else if (parameters.length == 3 && !(parameters[2].toUpperCase().matches("QUEEN|ROOK|BISHOP|KNIGHT"))) {
            return "Please provide a valid promotion type if applicable. Ex: queen";
        }
        else {
            ChessPosition from = new ChessPosition(parameters[0].charAt(1) - '0', parameters[0].charAt(0) - ('a'-1));
            ChessPosition to = new ChessPosition(parameters[1].charAt(1) - '0', parameters[1].charAt(0) - ('a'-1));
            if (parameters.length == 3) {
                ChessPiece.PieceType promotionType = ChessPiece.PieceType.valueOf(parameters[2].toUpperCase());
                server.makeMove(gameID, new ChessMove(from, to, promotionType));
                return parameters[0] + " to " + parameters[1] + " " + parameters[2];
            }
            else {
                server.makeMove(gameID, new ChessMove(from, to, null));
                return parameters[0] + " to " + parameters[1];
            }
        }
    }

    private String highlight(String[] parameters) {
        if (parameters.length != 1) {
            return "Please provide 1 parameter: coordinate to highlight moves for";
        }
        else if (!(parameters[0].matches("[a-h][1-8]"))) {
            return "Please provide position as a lowercase-letter number pair. Ex: d4";
        }
        else {
            ChessPosition highlightPosition = new ChessPosition(parameters[0].charAt(1) - '0', parameters[0].charAt(0) - ('a'-1));
            boardPrinter.printBoard(teamColor, highlightPosition);
            return "";
        }
    }

    private String resign() { //This is breaking through the class's original responsibility
        out.println("Are you sure you want to resign? Enter yes to confirm");
        String confirmation = new Scanner(System.in).nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            server.resign(gameID);
            return "Game resigned";
        }
        else {
            return "Resignation canceled";
        }
    }

    private String leave() throws ResponseException {
        server.leave(gameID);
        changeClientLayer = -1;
        return "Successfully left game!";
    }

    private String help() {
        return """
                move <FROM> <TO> <PROMOTION_PIECE> - make a move (Promotion piece should only be used to promote a pawn)
                highlight <COORDINATE> - highlight all legal moves for piece at the given coordinate
                redraw - redraw the game board
                resign - forfeit this game
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
