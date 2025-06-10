package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData addGame(GameData gameData) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    Collection<Integer> getGameIDs() throws DataAccessException;
    GameData getGameByID(int gameID) throws DataAccessException;
    //might want other ways to get a game? Perhaps by gameName or usernames?
    GameData updateGame(GameData gameData) throws DataAccessException;
    void clearGames() throws DataAccessException;
}
