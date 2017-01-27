package pl.airport.flights;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.airport.model.Lot;
import pl.airport.server.Server;

public class Departures {
	private final static Logger logger = LoggerFactory.getLogger(Departures.class);
	private static List<Lot> departuresArray = Collections.synchronizedList(new ArrayList<Lot>());
	public static LocalDateTime departuresLastTime = null;
	
	public static void appendAllDepartures(StringBuilder message) {
		if(departuresLastTime!=null) {
			message.append("T;"+departuresLastTime.toString()+Commons.END);
		}
		synchronized(departuresArray) {
			for(Lot lot : departuresArray) {
				message.append("D;N;"+lot.getEncodedString()+Commons.END);
			}
		}
	}
	
	public synchronized void checkDepartures(List<Lot> departures) {
		if (departures != null && !departures.isEmpty()) {
			compareDepartures(departures);
		}
	}
	
	private Lot getDeparture(String ID) {
		synchronized(departuresArray) {
			for(Lot lot : departuresArray) {
				if(lot.getID().equals(ID)) {
					return lot;
				}
			}
		}
		return null;
	}
		
	public void compareDepartures(List<Lot> currentList) {
		if(!departuresArray.isEmpty()) {
			logger.info("Last departures : "+departuresArray);
			logger.info("Current departures : "+currentList);
			long start = System.currentTimeMillis();
			HashMap<String, Boolean> used = new HashMap<>();
			synchronized(departuresArray) {
				for(Lot lot : departuresArray) {
					used.put(lot.getID(), false);
				}
			}
			HashMap<Character, ArrayList<Lot>> wyniki = new HashMap<>();
			wyniki.put(Commons.UPDATE, new ArrayList<Lot>());
			wyniki.put(Commons.SAME, new ArrayList<Lot>());
			wyniki.put(Commons.NEW, new ArrayList<Lot>());
			wyniki.put(Commons.REMOVED, new ArrayList<Lot>());
			for(Lot curr : currentList) {
				Lot result = getDeparture(curr.getID());
				if(result!=null) {
					if(!result.equals(curr)) {
						wyniki.get(Commons.UPDATE).add(curr);
					} else {
						wyniki.get(Commons.SAME).add(curr);
					}
					used.put(curr.getID(), true);
				} else {
					wyniki.get(Commons.NEW).add(curr);
				}
			}
			logger.info("Used : "+used.toString());
			Iterator<String> it = used.keySet().iterator();
			while(it.hasNext()) {
				String lotID = it.next();
				if(!used.get(lotID)) {
					Lot result = getDeparture(lotID);
					if(result!=null) {
						wyniki.get(Commons.REMOVED).add(result);
					}
				}
			}
			long result = System.currentTimeMillis() - start;
			logger.info("Comparing took " + result + " ms");
			logger.info(wyniki.toString());

			String message = "";
			if (departuresLastTime != null) {
				message += "T;" + departuresLastTime.toString() + Commons.END;
			}
			DeparturesConsumer konsument = wyniki.entrySet()
											.stream()
											.filter(item -> !item.getKey().equals(Commons.SAME))
											.collect(DeparturesConsumer::new, DeparturesConsumer::accept, DeparturesConsumer::combine);
			
			if (konsument.message != null && !konsument.message.toString().equals("")) {
				logger.info("CONSUMER : " + konsument.message.toString());
				message += konsument.message.toString();
				Server.broadcastMessage(message);
			}
			departuresArray.clear();
		}
		departuresArray.addAll(currentList);
	}

	class DeparturesConsumer implements Consumer<Entry<Character, ArrayList<Lot>>> {
		private StringBuilder message = new StringBuilder();	
		@Override
		public void accept(Entry<Character, ArrayList<Lot>> entry) {
			for(Lot lot : entry.getValue()) {
				logger.info(entry.getKey()+";"+lot.getEncodedString());
				message.append("D;"+entry.getKey()+";"+lot.getEncodedString()+Commons.END);	 
			}
		}
		public void combine(DeparturesConsumer other) {
			message.append(other.message);
		}
	}
}
