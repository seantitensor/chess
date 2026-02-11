package dataaccess.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dataaccess.DataAccessException;
import model.GameData;

public class LocalGameDAO implements GameDAO{
    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        int gameID = Math.abs(UUID.randomUUID().hashCode());
        games.put(gameID, game);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> getGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        games.put(game.gameID(),game);
    }

}
