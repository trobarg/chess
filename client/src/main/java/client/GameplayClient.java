package client;

import java.util.Arrays;

public class GameplayClient implements Client {
    private int changeClientLayer = 0;
    private final ServerFacade server;

    public GameplayClient(ServerFacade server) {
        this.server = server;
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

    public int changeClientLayer() {
        return changeClientLayer;
    }
}
