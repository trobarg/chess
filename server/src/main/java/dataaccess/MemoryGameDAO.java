package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public GameData addGame(GameData gameData) {
        return games.put(gameData.gameID(), gameData);
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
    public GameData updateGame(GameData gameData) { //functionally identical to adding a game?
        return games.put(gameData.gameID(), gameData);
    }

    @Override
    public void clearGames() {
        games.clear();
    }


}
