package service;
import java.util.HashSet;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.Collection;
import java.util.Random;

public class GameService {
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();

}
