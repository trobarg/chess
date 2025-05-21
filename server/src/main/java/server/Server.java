package server;

import com.google.gson.Gson;

import dataaccess.*;
import handler.*;
import model.ResponseExceptionContent;
import service.*;
import spark.*;

public class Server {
    private final UserHandler userHandler = new UserHandler(new UserService(new MemoryUserDAO(), new MemoryAuthDAO()));
    private final GameHandler gameHandler = new GameHandler(new GameService(new MemoryGameDAO(), new MemoryAuthDAO()));
    private final ClearHandler clearHandler = new ClearHandler(new ClearService(new MemoryUserDAO(), new MemoryGameDAO(), new MemoryAuthDAO()));

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
