package ui;

import client.*;

import java.util.Scanner;

import static java.lang.System.out;

public class REPL {
    private Client client;
    //should there be three client objects which are assigned to client?
    private final ServerFacade server;

    public REPL(ServerFacade server) {
        this.client = new PreloginClient(server);
        this.server = server;
    }

    public void run() {
        out.println("Welcome to the Chess Client! Sign in to get started.");
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
                        case PreloginClient preloginClient -> {
                            if (changeClientLayer == 1) { //nesting depth issue?
                                client = new PostloginClient(server);
                            }
                        }
                        case PostloginClient postloginClient -> {
                            if (changeClientLayer == 1) {
                                client = new GameplayClient(server);
                            } else if (changeClientLayer == -1) {
                                client = new PreloginClient(server);
                            }
                        }
                        case GameplayClient gameplayClient -> {
                            if (changeClientLayer == -1) {
                                client = new PostloginClient(server);
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
    }
}
