package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public GameData addGame(GameData game) {
        return games.put(game.gameID(), game);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public Collection<Integer> getGameIDs() {
        return games.keySet();
    }

    @Override
    public GameData getGameByID(int gameID) {
        return games.get(gameID);
    }

    @Override
    public GameData updateGame(GameData game) { //functionally identical to adding a game?
        return games.put(game.gameID(), game);
    }

    @Override
    public GameData DeleteGameByID(int gameID) {
        return games.remove(gameID);
    }

    @Override
    public void clearGames() {
        games.clear();
    }


}
