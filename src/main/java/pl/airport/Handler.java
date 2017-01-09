package pl.airport;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class Handler {
	private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User" + Server.nextUserNumber++;
        Server.userUsernameMap.put(user, username);
        Server.broadcastMessage(sender = "Server", msg = (username + " joined the chat"));
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Server.userUsernameMap.get(user);
        Server.userUsernameMap.remove(user);
        Server.broadcastMessage(sender = "Server", msg = (username + " left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	Server.broadcastMessage(sender = Server.userUsernameMap.get(user), msg = message);
    }
}
