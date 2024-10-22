package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData addGame(GameData game) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    GameData getGameByID(int gameID) throws DataAccessException;
    //might want other ways to get a game? Perhaps by gameName or usernames?
    GameData DeleteGameByID(int gameID) throws DataAccessException;
    void clearGames() throws DataAccessException;
}
