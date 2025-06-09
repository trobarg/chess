package ui;

import client.*;
import websocket.messages.Error;//so the compiler doesn't complain about naming collision with java.lang.Error
import websocket.messages.*;

import java.util.Scanner;

import static java.lang.System.out;

public class REPL implements ServerMessageHandler {
    private BoardPrinter boardPrinter;
    private Client client;
    private PreloginClient preloginClient;
    private PostloginClient postloginClient;
    private GameplayClient gameplayClient;
    private final ServerFacade server;

    public REPL(String urlExtension) {
        this.server = new ServerFacade(urlExtension, this);
        preloginClient = new PreloginClient(server);
        postloginClient = new PostloginClient(server);
        client = preloginClient;
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
                checkChangeClientLayer();
            }
            catch (Exception exception) {
                out.print(exception.getMessage());
            }
            out.println();
        }
        out.println("Goodbye!");
    }

    private void checkChangeClientLayer() throws Exception {
        int changeClientLayer = client.getChangeClientLayer();
        if (changeClientLayer != 0) {
            switch (client) {
                case PreloginClient ignored -> {
                    if (changeClientLayer == 1) {
                        client.resetChangeClientLayer();
                        client = postloginClient;
                    }
                }
                case PostloginClient ignored -> {
                    if (changeClientLayer == 1) {
                        client.resetChangeClientLayer();
                        out.println();
                        gameplayClient = new GameplayClient(server, postloginClient.getTargetGameIndex(),
                                postloginClient.getTargetGameColor());
                        client = gameplayClient;
                    } else if (changeClientLayer == -1) {
                        client.resetChangeClientLayer();
                        client = preloginClient;
                    }
                }
                case GameplayClient ignored -> {
                    if (changeClientLayer == -1) {
                        client.resetChangeClientLayer();
                        client = postloginClient;
                    }
                }
                case null, default -> throw new Exception("Invalid client layer change");
            }
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        if (serverMessage instanceof Error) {
            out.println(((Error) serverMessage).getMessage());
        }
        else if (serverMessage instanceof Notification) {
            out.println(((Notification) serverMessage).getMessage());
        }
        else if (serverMessage instanceof LoadGame) {
            gameplayClient.updateAndPrintBoard(((LoadGame) serverMessage).getGame());
        }
    }
}
