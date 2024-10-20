package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    @Override
    public void addGame(GameData game) throws DataAccessException {

    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public GameData getGameByID(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void DeleteGameByID(int gameID) throws DataAccessException {

    }
}
