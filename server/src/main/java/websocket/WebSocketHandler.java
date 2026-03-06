package websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import chess.ChessGame;
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
            String username = getUsername(command.getAuthToken());

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
            session.getRemote().sendString(errorMessage.toString());
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
        var loadGame = new LoadGameMessage(gameData.game());
        session.getRemote().sendString(gson.toJson(loadGame));

        var message = String.format("%s Has entered the match.", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification);
    }

    private void makeMove(Session session, MakeMoveCommand command, String username) throws IOException {
        connections.add(command.getGameID(), session);
        

        GameData gameData = gameDAO.getGame(command.getGameID());
        ChessGame.TeamColor currentTurn = gameData.game().getTeamTurn();
        String currentUser = (currentTurn == ChessGame.TeamColor.WHITE) 
                        ? gameData.whiteUsername() 
                        : gameData.blackUsername();

        if (!username.equals(currentUser)) {
            throw new UnauthorizedException("It's not your turn, " + username + "!");
        }
        
        try { 
            gameData.game().makeMove(command.getMove());
        } catch (InvalidMoveException e) {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Illegal move!")));
        }
        gameDAO.updateGame(gameData);
        var loadGame = new LoadGameMessage(gameData.game());
        session.getRemote().sendString(gson.toJson(loadGame));

        connections.broadcast(command.getGameID(), null, loadGame);

        var message = String.format("%s Has made a move!", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(command.getGameID(), session, notification);
    }

    private void leave(Session session, Integer gameID, String username) throws IOException {
        connections.removeGame(gameID, session);
        var message = String.format("%s Has left the match.", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification);
    }

    private void resign(Session session, Integer gameID, String username) throws IOException {
        connections.removeGame(gameID, session);
        var message = String.format("%s Has resigned.", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification);
    }

    private String getUsername(String authToken) {
        return authDAO.getAuthData(authToken).username();
    }
}