package handler;

import com.google.gson.Gson;

import java.util.Random;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;

public class GameHandler {
private final GameDAO gameDAO = new MemoryGameDAO();

}
