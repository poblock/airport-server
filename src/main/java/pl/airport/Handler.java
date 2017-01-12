package pl.airport;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket
public class Handler {
	final Logger logger = LoggerFactory.getLogger(Handler.class);
	
    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        String username = "User" + Server.nextUserNumber++;
        Server.userUsernameMap.put(user, username);
        Server.broadcastMessage("Server", username + " joined the chat");
        Server.welcomeUser(user);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
    	logger.info("WEBSOCKET ON CLOSE : "+user+" statusCode: "+statusCode+" reason : "+reason);
        String username = Server.userUsernameMap.get(user);
        Server.userUsernameMap.remove(user);
        Server.broadcastMessage("Server", username + " left the chat");
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	Server.broadcastMessage(Server.userUsernameMap.get(user), message);
    }
}
