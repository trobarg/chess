package server;

import com.google.gson.Gson;

import dataaccess.*;
import handler.*;
import model.ResponseExceptionContent;
import service.*;
import spark.*;

public class Server {
    private final SQLUserDAO userDAO = new SQLUserDAO();
    private final SQLGameDAO gameDAO = new SQLGameDAO();
    private final SQLAuthDAO authDAO = new SQLAuthDAO();
    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final UserService userService = new UserService(userDAO, authDAO);
    private final ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
    private final UserHandler userHandler = new UserHandler(userService);
    private final GameHandler gameHandler = new GameHandler(gameService);
    private final ClearHandler clearHandler = new ClearHandler(clearService);

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", clearHandler::clearApplication);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);
        Spark.get("/game", gameHandler::listGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);
        Spark.exception(ResponseException.class, this::exceptionHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void exceptionHandler(ResponseException rE, Request req, Response res) {
        res.status(rE.statusCode());
        res.body(new Gson().toJson(new ResponseExceptionContent(rE.getMessage())));
    }


    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
