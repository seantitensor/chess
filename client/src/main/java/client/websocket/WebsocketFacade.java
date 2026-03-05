package client.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.Gson;

import chess.ChessMove;
import exception.ResponseException;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;

public class WebsocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;
    private final Gson gson = new Gson();

    public WebsocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case ServerMessageType.LOAD_GAME -> {
                            LoadGameMessage loadGame = gson.fromJson(message, LoadGameMessage.class);
                            notificationHandler.notify(loadGame);
                        }
                        case ServerMessageType.ERROR -> {
                            ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
                            notificationHandler.notify(error);
                        }
                        case ServerMessageType.NOTIFICATION -> {
                            NotificationMessage notification = gson.fromJson(message, NotificationMessage.class);
                            notificationHandler.notify(notification);
                        }
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // commands
    public void connect(String authToken, Integer gameID) throws ResponseException {
        sendMessage(new UserGameCommand(UserGameCommand.CommandType.CONNECT,authToken,gameID));
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        sendMessage(new MakeMoveCommand(authToken, gameID, move));
    }

    public void leave(String authToken, Integer gameID) throws ResponseException {
        sendMessage(new UserGameCommand(UserGameCommand.CommandType.LEAVE,authToken,gameID));
    }

    public void resign(String authToken, Integer gameID) throws ResponseException {
        sendMessage(new UserGameCommand(UserGameCommand.CommandType.RESIGN,authToken,gameID));
    }

    // helper function to abstract code
    private void sendMessage(UserGameCommand command) throws ResponseException {
        try {
            String jsonCommand = gson.toJson(command);
            this.session.getBasicRemote().sendText(jsonCommand);
        } catch (IOException e) {
            throw new ResponseException(ResponseException.Code.ClientError, "couldn't send message: " + e.getMessage());
        }
    }
}