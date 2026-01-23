package services;

import dataaccess.game.GameDAO;

public class GameService {
    private final GameDAO gameDAO;
    
    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public void clearDB() {
        gameDAO.clear();
    }
}
