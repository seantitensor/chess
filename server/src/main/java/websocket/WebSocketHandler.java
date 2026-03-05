package websocket;

import com.google.gson.Gson;

import exceptions.UnauthorizedException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private static final Gson gson = new Gson();

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
            gamedId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameId, session);

            switch(command.getCommandType()) {
                case CONNECT -> connect(session, gameId, username);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leave(session, gameId, username);
                case RESIGN -> resign(session, gameId, username);
            }
        } catch (UnauthorizedException ex) {
            sendMessage(session, gameId, new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session, gameId, new ErrorMessage("Error: " + ex.getMessage()));
        }

    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, Integer gameID, String username) throws IOException {
        connections.add(gameID, session);
        var message = String.format("%s Has entered the match.", username);
        var notification = new LoadGameMessage(message);
        connections.broadcast(gameID, session, notification);
    }

    private void makeMove(Session session, Integer gameID, String username) throws IOException {
        connections.add(gameID, session);
        var message = String.format("%s Has enter the match", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification);
    }

    private void leave(Session session, Integer gameID, String username) throws IOException {
        connections.remove(gameID, session);
        var message = String.format("%s Has left the match.", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification);
    }

    private void resign(Session session, Integer gameID, String username) throws IOException {
        connections.remove(gameID, session);
        var message = String.format("%s Has resigned.", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(gameID, session, notification);
    }
}