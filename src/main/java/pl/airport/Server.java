package pl.airport;

import static spark.Spark.init;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

public class Server {
	// this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static int nextUserNumber = 1; //Assign to username for next connecting user
    static Scheduler scheduler;
   
    public static void main(String[] args) {
        staticFiles.location("/public"); //index.html is served at localhost:4567 (default port)
        staticFiles.expireTime(600);
        webSocket("/chat", Handler.class);
        init();
        scheduler = new Scheduler();
    }
    
    public static void welcomeUser(Session user) {
    	try {
        	user.getRemote().sendString(String.valueOf(new JSONObject().put("msg", Flights.getAllFlights())));
    	} catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Sends a message from one user to all users, along with a list of current usernames
    public static void broadcastMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject().put("msg", message)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
