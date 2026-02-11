package dataaccess.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SqlDAO;
import model.GameData;

public class SqlGameDAO extends SqlDAO implements GameDAO {

    private static final Gson gson = new Gson();

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        try {
            executeUpdate(
                statement,
                game.whiteUsername(),
                game.blackUsername(),
                game.gameName(),
                gson.toJson(game.game()),
                game.gameID()
            );
        } catch (DataAccessException e) {
            throw new DataAccessException("couldn't connect to the db");
        }
    }

    @Override
    public Collection<GameData> getGames() throws DataAccessException {
        var games = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) { 
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        var gameObj = gson.fromJson(rs.getString("game"), ChessGame.class);
                        games.add(new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            gameObj
                        ));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("couldn't connect to the db");
        }
        return games;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?";  
             try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var gameObj = gson.fromJson(rs.getString("game"), ChessGame.class);
                        return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            gameObj
                        );
                    }
                }
            }        
        } catch (Exception e) {
            throw new DataAccessException("couldn't connect to the db");
        }
        return null;
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        var gameObj = gson.toJson(game.game());
        try {
            return executeUpdate(
                statement,
                game.whiteUsername(),
                game.blackUsername(),
                game.gameName(),
                gameObj
            );
        } catch (DataAccessException e) {
            throw new DataAccessException("couldn't connect to the db");
        }
    }

    @Override
    public void clear() {
        var statement = "TRUNCATE games";
        executeUpdate(statement);        
    }
}
