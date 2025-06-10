import client.ServerFacade;
import exception.ResponseException;
import ui.REPL;

public class Main {
    public static void main(String[] args) {
        var serverUrlExtension = "localhost:8080";
        if (args.length == 1) {
            serverUrlExtension = args[0];
        }
        new REPL(serverUrlExtension).run();
    }
}