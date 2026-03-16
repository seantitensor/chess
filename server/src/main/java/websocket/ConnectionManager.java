package websocket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import websocket.messages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    public void add(Integer gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> new HashSet<>()).add(session);
    }  

    public void remove(Session session) {
        for (var entry : connections.entrySet()) {
            Set<Session> sessions = entry.getValue();
            if (sessions.remove(session)) {
                if (sessions.isEmpty()) {
                    connections.remove(entry.getKey());
                }
                break;
            }
        }
    }

    public void broadcast(Integer gameID, Session excludeSession, ServerMessage serverMessage) throws IOException {
        String msg = gson.toJson(serverMessage);
        Set<Session> sessionSet  = connections.get(gameID);
        if (sessionSet == null) { return;}
        for (Session c : sessionSet) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}