package websocket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

import websocket.messages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> new HashSet<>()).add(session);
    }  

    public void removeGame(Integer gameID, Session session) {
        Set<Session> sessionSet  = connections.get(gameID);
        if (sessionSet != null) {
            sessionSet.remove(session);
            if (sessionSet.isEmpty()) {
                connections.remove(gameID);
            }
        }
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
        String msg = serverMessage.toString();
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