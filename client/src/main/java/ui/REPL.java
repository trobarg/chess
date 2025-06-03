package ui;

import client.Client;
import client.PreloginClient;

import java.util.Scanner;

import static java.lang.System.out;

public class REPL {
    private Client client; //REPL will need to change between clients

    public REPL(String serverUrl) {
        this.client = new PreloginClient(serverUrl);
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
            }
            catch (Exception exception) {
                out.print(exception.getMessage());
            }
            out.println();
        }
    }
}
