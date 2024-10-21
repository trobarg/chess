package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void addGame(GameData game) throws DataAccessException {
        int gameID = game.GameID();
        if (games.containsKey(gameID)) {
            throw new DataAccessException("Game already exists");
        }
        games.put(gameID, game);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public GameData getGameByID(int gameID) throws DataAccessException {
        if (games.containsKey(gameID)) {
            return games.get(gameID);
        }
        throw new DataAccessException("Game does not exist");
    }

    @Override
    public void DeleteGameByID(int gameID) throws DataAccessException {
        if (games.containsKey(gameID)) {
            games.remove(gameID);
        }
        throw new DataAccessException("Game does not exist");
    }

    @Override
    public void clearGames() {
        games.clear();
    }


}
