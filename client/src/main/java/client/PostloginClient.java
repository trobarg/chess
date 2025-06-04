package client;

import exception.ResponseException;
import model.CreateGameResult;
import model.GameData;

import java.util.Arrays;
import java.util.HashSet;

public class PostloginClient implements Client {
    private int changeClientLayer = 0;
    private final ServerFacade server;

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
        HashSet<GameData> games = server.listGames();
        StringBuilder sb = new StringBuilder();
        for (GameData game : games) {
            int id = game.gameID();
            String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
            String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
            sb.append(String.format("%d -- Game Name: %s  |  White User: %s  |  Black User: %s%n",
                    id, game.gameName(), whiteUser, blackUser));
        }
        return sb.toString();
    }


    private String create(String[] parameters) throws ResponseException { //do these have to be arrays of Strings?
        if (parameters.length != 1) {
            return "Please provide 1 parameter: game name";
        }
        else {
            CreateGameResult createGameResult = server.createGame(parameters[0]);
            return "Successfully created game!"; //is the gameID needed past this point?
        }
    }

    private String join(String[] parameters) throws ResponseException {
        if (parameters.length != 2) {
            return "Please provide 2 parameters: game ID and color";
        }
        else {
            server.joinGame(Integer.parseInt(parameters[0]), parameters[1]);
            changeClientLayer = 1;
            return "Successfully joined game!";
        }
    }

    private String observe(String[] parameters) {
        if (parameters.length != 1) {
            return "Please provide 1 parameter: game ID";
        }
        else {
            // don't think the program actually supports observing games yet
            changeClientLayer = 1;
            return "Observing game!";
        }
    }

    private String help() {
        return """
                create <NAME> - create a new game
                list - list all games
                join <ID> [WHITE|BLACK] - join a game as color
                observe <ID> - observe a game
                logout - log out of current user
                quit - quit the program
                help - display this message
               """;
    }

    public int changeClientLayer() {
        return changeClientLayer;
    }
}
