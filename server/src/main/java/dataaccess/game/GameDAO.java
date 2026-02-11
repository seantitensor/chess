package dataaccess.game;

import dataaccess.DataAccessException;
import model.GameData;

public interface GameDAO {
    //join
    void updateGame(GameData game) throws DataAccessException;
    //get all games
    java.util.Collection<GameData> getGames() throws DataAccessException;
    //get one game
    GameData getGame(int gameID) throws DataAccessException;
    //create game
    int createGame(GameData game) throws DataAccessException;
    //clear games
    void clear();
}
