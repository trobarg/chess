package ui;

import client.*;

import java.util.Scanner;

import static java.lang.System.out;

public class REPL {
    private Client client;
    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private final GameplayClient gameplayClient;
    private final ServerFacade server;

    public REPL(ServerFacade server) {
        preloginClient = new PreloginClient(server);
        postloginClient = new PostloginClient(server);
        gameplayClient = new GameplayClient(server);
        client = preloginClient;
        this.server = server;
    }

    public void run() {
        out.println("Welcome to the Chess Client! Enter help to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                out.print(result);
                int changeClientLayer = client.changeClientLayer();
                if (changeClientLayer != 0) {
                    switch (client) {
                        case PreloginClient ignored -> {
                            if (changeClientLayer == 1) { //nesting depth issue?
                                client = postloginClient;
                            }
                        }
                        case PostloginClient ignored -> {
                            if (changeClientLayer == 1) {
                                client = gameplayClient;
                            } else if (changeClientLayer == -1) {
                                client = preloginClient;
                            }
                        }
                        case GameplayClient ignored -> {
                            if (changeClientLayer == -1) {
                                client = postloginClient;
                            }
                        }
                        case null, default -> throw new Exception("Invalid client layer change");
                    }
                }
            }
            catch (Exception exception) {
                out.print(exception.getMessage());
            }
            out.println();
        }
        out.println("Goodbye!");
    }
}
