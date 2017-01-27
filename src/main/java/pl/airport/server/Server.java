package pl.airport.server;

import static spark.Spark.init;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;
import static spark.Spark.webSocketIdleTimeoutMillis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import pl.airport.flights.Arrivals;
import pl.airport.flights.Departures;
import pl.airport.parser.Scheduler;

public class Server {
	// this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static int nextUserNumber = 1; //Assign to username for next connecting user
   
    public static void main(String[] args) {
        staticFiles.location("/public"); //index.html is served at localhost:4567 (default port)
        staticFiles.expireTime(600);
        webSocket("/airport", Handler.class);
        webSocketIdleTimeoutMillis(60*60*1000);
        init();
        
        Scheduler scheduler = new Scheduler();
    }
    
    public static void welcomeUser(Session user) {
    	try {
        	user.getRemote().sendString(prepareWelcomeMessage());
    	} catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void broadcastMessage(String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(makeJSONMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private static String prepareWelcomeMessage() throws JSONException {
    	StringBuilder message = new StringBuilder();
    	Arrivals.appendAllArrivals(message);
    	Departures.appendAllDepartures(message);
    	return makeJSONMessage(message.toString());
    }
    
    private static String makeJSONMessage(String message) throws JSONException {
    	return String.valueOf(new JSONObject().put("msg", message));
    }
}
