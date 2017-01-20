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
        Server.welcomeUser(user);
        logger.info("ONCONNECT USER: "+username);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
    	logger.info("WEBSOCKET ON CLOSE : "+user+" statusCode: "+statusCode+" reason : "+reason);
        Server.userUsernameMap.remove(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	String u = Server.userUsernameMap.get(user);
    	logger.info("ONMESSAGE from User : "+u+" "+user);
    }
}
