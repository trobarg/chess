package client;

import chess.ChessGame;
import exception.ResponseException;
import model.CreateGameResult;
import model.GameData;

import java.util.*;

public class PostloginClient implements Client {
    private int changeClientLayer = 0;
    private final ServerFacade server;
    private int targetGameIndex;
    private ChessGame.TeamColor targetGameColor;

    public PostloginClient(ServerFacade server) {
        this.server = server;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var command = (tokens.length > 0) ? tokens[0] : "help";
            var parameters = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (command) {
                case "logout" -> logout();
                case "list" -> list();
                case "create" -> create(parameters);
                case "join" -> join(parameters);
                case "observe" -> observe(parameters);
                case "quit" -> "quit";
                default -> help();
            };
        }
        catch (Exception exception) {
            return exception.getMessage();
        }
    }

    private String logout() throws ResponseException {
        server.logout();
        changeClientLayer = -1;
        return "Successfully logged out!";
    }

    private String list() throws ResponseException {
        ArrayList<GameData> gamesList = (ArrayList<GameData>) server.listGames();
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (GameData game : gamesList) {
            String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
            String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
            sb.append(String.format("%d -- Game Name: %s  |  White User: %s  |  Black User: %s%n",
                    i, game.gameName(), whiteUser, blackUser));
            i++;
        }
        return sb.toString();
    }


    private String create(String[] parameters) throws ResponseException {
        if (parameters.length != 1) {
            return "Please provide 1 parameter: game name";
        }
        else {
            server.createGame(parameters[0]);
            return "Successfully created game!";
        }
    }

    private String join(String[] parameters) throws ResponseException {
        if (parameters.length != 2) {
            return "Please provide 2 parameters: game number and color";
        }
        else {
            server.joinGame(Integer.parseInt(parameters[0]), parameters[1]);
            changeClientLayer = 1;
            targetGameIndex = Integer.parseInt(parameters[0]);
            targetGameColor = ChessGame.TeamColor.valueOf(parameters[1].toUpperCase());
            return "Successfully joined game!";
        }
    }

    private String observe(String[] parameters) {
        if (parameters.length != 1) {
            return "Please provide 1 parameter: game number";
        }
        else {
            // don't think the program actually supports observing games yet
            changeClientLayer = 1;
            targetGameIndex = Integer.parseInt(parameters[0]);
            targetGameColor = ChessGame.TeamColor.WHITE;
            return "Observing game!";
        }
    }

    private String help() {
        return """
                create <NAME> - create a new game
                list - list all games
                join <NUMBER> [WHITE|BLACK] - join a game as color
                observe <NUMBER> - observe a game
                logout - log out of current user
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

    public int getTargetGameIndex() {
        return targetGameIndex;
    }

    public ChessGame.TeamColor getTargetGameColor() {
        return targetGameColor;
    }
}
