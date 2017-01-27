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

public class Arrivals {
	private final static Logger logger = LoggerFactory.getLogger(Arrivals.class);
	private static List<Lot> arrivalsArray = Collections.synchronizedList(new ArrayList<Lot>());
	public static LocalDateTime arrivalsLastTime = null;

	public static void appendAllArrivals(StringBuilder message) {
		if(arrivalsLastTime!=null) {
			message.append("T;"+arrivalsLastTime.toString()+Commons.END);
		}
		synchronized(arrivalsArray) {
			for(Lot lot : arrivalsArray) {
				message.append("A;N;"+lot.getEncodedString()+Commons.END);
			}
		}
	}
	
	public synchronized void checkArrivals(List<Lot> arrivals) {
		if (arrivals != null && !arrivals.isEmpty()) {
			compareArrivals(arrivals);
		}
	}
	
	private Lot getArrival(String ID) {
		synchronized(arrivalsArray) {
			for(Lot lot : arrivalsArray) {
				if(lot.getID().equals(ID)) {
					return lot;
				}
			}
		}
		return null;
	}
	
	public void compareArrivals(List<Lot> currentList) {
		if(!arrivalsArray.isEmpty()) {
			logger.info("Last arrivals : "+arrivalsArray);
			logger.info("Current arrivals : "+currentList);
			long start = System.currentTimeMillis();
			HashMap<String, Boolean> used = new HashMap<>();
			synchronized(arrivalsArray) {
				for(Lot lot : arrivalsArray) {
					used.put(lot.getID(), false);
				}
			}
			HashMap<Character, ArrayList<Lot>> wyniki = new HashMap<>();
			wyniki.put(Commons.UPDATE, new ArrayList<Lot>());
			wyniki.put(Commons.SAME, new ArrayList<Lot>());
			wyniki.put(Commons.NEW, new ArrayList<Lot>());
			wyniki.put(Commons.REMOVED, new ArrayList<Lot>());
			for(Lot curr : currentList) {
				Lot result = getArrival(curr.getID());
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
					Lot lot = getArrival(lotID);
					if(lot!=null) {
						wyniki.get(Commons.REMOVED).add(lot);
					}
				}
			}
			long result = System.currentTimeMillis() - start;
			logger.info("Comparing took "+result+" ms");
			logger.info(wyniki.toString());	
			String message = "";
			if(arrivalsLastTime!=null) {
				message+="T;"+arrivalsLastTime.toString()+Commons.END;
			}
			ArrivalsConsumer konsument = wyniki.entrySet()
											.stream()
											.filter(item -> !item.getKey().equals(Commons.SAME))
											.collect(ArrivalsConsumer::new, ArrivalsConsumer::accept, ArrivalsConsumer::combine);
			if(konsument.message!=null && !konsument.message.toString().equals("")) {
				logger.info("CONSUMER : "+konsument.message.toString());
				message+=konsument.message.toString();
				Server.broadcastMessage(message);
			}
			arrivalsArray.clear();
		} 
		arrivalsArray.addAll(currentList);
	}
	
	class ArrivalsConsumer implements Consumer<Entry<Character, ArrayList<Lot>>> {
		private StringBuilder message = new StringBuilder();	
		@Override
		public void accept(Entry<Character, ArrayList<Lot>> entry) {
			for(Lot lot : entry.getValue()) {
				logger.info(entry.getKey()+";"+lot.getEncodedString());
				message.append("A;"+entry.getKey()+";"+lot.getEncodedString()+Commons.END);	 
			}
		}
		public void combine(ArrivalsConsumer other) {
			message.append(other.message);
		}
	}
}
