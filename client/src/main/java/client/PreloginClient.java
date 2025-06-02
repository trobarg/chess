package client;

import exception.ResponseException;

import java.util.Arrays;

public class PreloginClient {

    private final ServerFacade server;
    private final String serverUrl;

    public PreloginClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var command = (tokens.length > 0) ? tokens[0] : "help";
            var parameters = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (command) {
                case "register" -> register(parameters);
                case "login" -> login(parameters);
                case "quit" -> "quit";
                default -> help();
            };


        } catch (Exception exception) {
            return exception.getMessage();
        }
    }

    private String register(String[] parameters) throws ResponseException {
        if (parameters.length != 3) {
            return "Please provide 3 parameters: username, password, and email";
        }
        else {
            server.register(parameters[0], parameters[1], parameters[2]); //catch handled in eval function
            return "Successfully registered and logged in!";
        }
    }

    private String login(String[] parameters) throws ResponseException {
        if (parameters.length != 2) {
            return "Please provide 2 parameters: username and password";
        }
        else {
            server.login(parameters[0], parameters[1]);
            return "Successfully logged in!";
        }
    }

    private String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - create a new user
                login <USERNAME> <PASSWORD> - login as an existing user
                quit - stop playing
                help - display this message
                """;
    }
}
