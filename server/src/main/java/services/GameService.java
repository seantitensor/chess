package services;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessGame;
import dataaccess.auth.AuthDAO;
import dataaccess.game.GameDAO;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import request.JoinGameRequest;
import response.ListGameResult;
import response.ListResult;
import response.NewGameResult;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListResult getListGames(String authToken){
        if (authDAO.getAuthData(authToken) == null) {
            throw new UnauthorizedException("Unauthorized ");
        }
        return new ListResult(dropGame(gameDAO.getGames()));
    }

    public void joinGame(String authToken, JoinGameRequest req){
        AuthData auth = authDAO.getAuthData(authToken);
        if (auth == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        GameData game = gameDAO.getGame(req.gameID());

        if (game == null) {
            throw new BadRequestException("No game with that ID");
        }

        if (req.playerColor() == null) {
            throw new BadRequestException("No color for user");
        }

        String username = (req.playerColor() == ChessGame.TeamColor.BLACK) 
                                                    ? game.blackUsername() 
                                                    : game.whiteUsername();
        if ( username != null) {
                throw new AlreadyTakenException("Color is already Taken");
        }

        if (req.playerColor() == ChessGame.TeamColor.BLACK) {
            GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            gameDAO.updateGame(updatedGame);
        } else {
            GameData updatedGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(updatedGame);
        }
    }

    public NewGameResult createGame(String authToken, String gameName) {
        if (authDAO.getAuthData(authToken) == null) {
            throw new UnauthorizedException("Unauthorized ");
        }
        GameData game = new GameData(0,null,null, gameName, new ChessGame());
        int gameID = gameDAO.createGame(game);
        return new NewGameResult(gameID);
    }

    public void clearDB() {
        gameDAO.clear();
    }

    private Collection<ListGameResult> dropGame(Collection<GameData> games) {
        Collection<ListGameResult> res = new ArrayList<>();
        for (GameData game : games) {
            res.add(new ListGameResult(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return res;
    }
}
