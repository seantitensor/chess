package websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.auth.AuthDAO;
import dataaccess.game.GameDAO;
import exceptions.UnauthorizedException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private static final Gson gson = new Gson();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        int gameId = -1;
        Session session = ctx.session;
        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String authToken = command.getAuthToken();
            var authData = authDAO.getAuthData(authToken);
            if (authData == null) {
                throw new UnauthorizedException("Bad auth");
            }
            String username = getUsername(authToken);

            switch(command.getCommandType()) {
                case CONNECT -> connect(session, gameId, username);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(session, moveCommand, username);
                }
                case LEAVE -> leave(session, gameId, username);
                case RESIGN -> resign(session, gameId, username);
            }
        } catch (UnauthorizedException authEx) {
            try {
                ErrorMessage errorMessage = new ErrorMessage(authEx.getMessage());
            session.getRemote().sendString(gson.toJson(errorMessage));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        connections.remove(ctx.session);
        System.out.println("Websocket closed");
    }

    private void connect(Session session, Integer gameID, String username) throws IOException {
        connections.add(gameID, session);
        
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid Game ID")));
            return;
        }
        var loadGame = new LoadGameMessage(gameData.game());
        session.getRemote().sendString(gson.toJson(loadGame));
        String message;
        if (username.equals(gameData.whiteUsername())) {
            message = String.format("%s joined the game as White.", username);
        } else if (username.equals(gameData.blackUsername())) {
            message = String.format("%s joined the game as Black.", username);
        } else {
            message = String.format("%s joined as an observer.", username);
        }
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification);
    }

    private void makeMove(Session session, MakeMoveCommand command, String username) throws IOException {
        // get info about turn
        var gameID = command.getGameID();
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame.TeamColor currentTurn = gameData.game().getTeamTurn();
        String currentUser = (currentTurn == ChessGame.TeamColor.WHITE) 
                        ? gameData.whiteUsername() 
                        : gameData.blackUsername();

        if (!username.equals(currentUser)) {
            throw new UnauthorizedException("It's not your turn, " + username + "!");
        }
        ChessGame.TeamColor opponentColor = (currentTurn == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        String opponentName = (opponentColor == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();

        // make move
        ChessMove move = command.getMove();
        try { 
            gameData.game().makeMove(move);
        } catch (InvalidMoveException e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Illegal move!")));
            return;
        }
        gameDAO.updateGame(gameData);
        var loadGame = new LoadGameMessage(gameData.game());
        session.getRemote().sendString(gson.toJson(loadGame));

        String moveMsg = String.format("%s moved from %s to %s", username, move.getStartPosition(), move.getEndPosition());
        connections.broadcast(gameID, session, new NotificationMessage(moveMsg));
        connections.broadcast(gameID, session, loadGame);

        if (gameData.game().isInCheckmate(opponentColor)) {
            connections.broadcast(gameID, null, new NotificationMessage(opponentName + " is in CHECKMATE!"));
        } else if (gameData.game().isInCheck(opponentColor)) {
            connections.broadcast(gameID, null, new NotificationMessage(opponentName + " is in CHECK!"));
        }
    }

    private void leave(Session session, Integer gameID, String username) throws IOException {
        connections.remove(session);
        var message = String.format("%s Has left the match.", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification);
        GameData gameData = gameDAO.getGame(gameID);
        if (username.equals(gameData.whiteUsername())) {
            GameData newGameData = new GameData(gameData.gameID(),null,gameData.blackUsername(), gameData.gameName(), gameData.game());
            gameDAO.updateGame(newGameData);
        } else if (username.equals(gameData.blackUsername())) {
            GameData newGameData = new GameData(gameData.gameID(),gameData.whiteUsername(),null, gameData.gameName(), gameData.game());
            gameDAO.updateGame(newGameData);
        }
    }

    private void resign(Session session, Integer gameID, String username) throws IOException {
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid Game ID")));
            return;
        }

        if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Observers cannot resign")));
            return;
        }
        var message = String.format("%s Has resigned.", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, null, notification);
    }

    private String getUsername(String authToken) {
        return authDAO.getAuthData(authToken).username();
    }
}