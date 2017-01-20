package pl.airport;

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

public class Flights {
	final static Logger logger = LoggerFactory.getLogger(Flights.class);
	private static List<Lot> arrivalsArray = Collections.synchronizedList(new ArrayList<Lot>());
	private static List<Lot> departuresArray = Collections.synchronizedList(new ArrayList<Lot>());
	public static LocalDateTime arrivalsLastTime = null;
	public static LocalDateTime departuresLastTime = null;
	public static final int ARRIVALS = 0;
	public static final int DEPARTURES = 1;
	
	public static final char UPDATE = 'U';
	public static final char NEW = 'N';
	public static final char REMOVED = 'R';
	public static final char SAME = 'S';
	public static final char END = '|';
	
	public static String getAllFlights() {
		StringBuilder message = new StringBuilder();
		if(arrivalsLastTime!=null) {
			message.append("T;"+arrivalsLastTime.toString()+END);
		}
		synchronized(arrivalsArray) {
			for(Lot lot : arrivalsArray) {
				message.append("A;N;"+lot.getEncodedString()+END);
			}
		}
		if(departuresLastTime!=null) {
			message.append("T;"+departuresLastTime.toString()+END);
		}
		synchronized(departuresArray) {
			for(Lot lot : departuresArray) {
				message.append("D;N;"+lot.getEncodedString()+END);
			}
		}
		logger.info("ALL FLIGHTS LENGTH : "+message.length());
		return message.toString();
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
	
	public synchronized void checkFlights(List<Lot> arrivals, List<Lot> departures) {
		if (arrivals != null && !arrivals.isEmpty()) {
			compareArrivals(arrivals);
		}
		if (departures != null && !departures.isEmpty()) {
			compareDepartures(departures);
		}
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
			wyniki.put(UPDATE, new ArrayList<Lot>());
			wyniki.put(SAME, new ArrayList<Lot>());
			wyniki.put(NEW, new ArrayList<Lot>());
			wyniki.put(REMOVED, new ArrayList<Lot>());
			for(Lot curr : currentList) {
				Lot result = getArrival(curr.getID());
				if(result!=null) {
					if(!result.equals(curr)) {
						wyniki.get(UPDATE).add(curr);
					} else {
						wyniki.get(SAME).add(curr);
					}
					used.put(curr.getID(), true);
				} else {
					wyniki.get(NEW).add(curr);
				}
			}
			logger.info("Used : "+used.toString());
			Iterator<String> it = used.keySet().iterator();
			while(it.hasNext()) {
				String lotID = it.next();
				if(!used.get(lotID)) {
					Lot lot = getArrival(lotID);
					if(lot!=null) {
						wyniki.get(REMOVED).add(lot);
					}
				}
			}
			long result = System.currentTimeMillis() - start;
			logger.info("Comparing took "+result+" ms");
			logger.info(wyniki.toString());	
			String message = "";
			if(arrivalsLastTime!=null) {
				message+="T;"+arrivalsLastTime.toString()+END;
			}
			ArrivalsConsumer konsument = wyniki.entrySet()
											.stream()
											.filter(item -> !item.getKey().equals(SAME))
											.collect(ArrivalsConsumer::new, ArrivalsConsumer::accept, ArrivalsConsumer::combine);
			logger.info("CONSUMER : "+konsument.message.toString());
			if(konsument.message!=null && !konsument.message.toString().equals("")) {
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
				message.append("A;"+entry.getKey()+";"+lot.getEncodedString()+END);	 
			}
		}
		public void combine(ArrivalsConsumer other) {
			message.append(other.message);
		}
	}
	
	class DeparturesConsumer implements Consumer<Entry<Character, ArrayList<Lot>>> {
		private StringBuilder message = new StringBuilder();	
		@Override
		public void accept(Entry<Character, ArrayList<Lot>> entry) {
			for(Lot lot : entry.getValue()) {
				logger.info(entry.getKey()+";"+lot.getEncodedString());
				message.append("D;"+entry.getKey()+";"+lot.getEncodedString()+END);	 
			}
		}
		public void combine(DeparturesConsumer other) {
			message.append(other.message);
		}
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
			wyniki.put(UPDATE, new ArrayList<Lot>());
			wyniki.put(SAME, new ArrayList<Lot>());
			wyniki.put(NEW, new ArrayList<Lot>());
			wyniki.put(REMOVED, new ArrayList<Lot>());
			for(Lot curr : currentList) {
				Lot result = getDeparture(curr.getID());
				if(result!=null) {
					if(!result.equals(curr)) {
						wyniki.get(UPDATE).add(curr);
					} else {
						wyniki.get(SAME).add(curr);
					}
					used.put(curr.getID(), true);
				} else {
					wyniki.get(NEW).add(curr);
				}
			}
			logger.info("Used : "+used.toString());
			Iterator<String> it = used.keySet().iterator();
			while(it.hasNext()) {
				String lotID = it.next();
				if(!used.get(lotID)) {
					Lot result = getDeparture(lotID);
					if(result!=null) {
						wyniki.get(REMOVED).add(result);
					}
				}
			}
			long result = System.currentTimeMillis() - start;
			logger.info("Comparing took " + result + " ms");
			logger.info(wyniki.toString());

			String message = "";
			if (departuresLastTime != null) {
				message += "T;" + departuresLastTime.toString() + END;
			}
			DeparturesConsumer konsument = wyniki.entrySet()
											.stream()
											.filter(item -> !item.getKey().equals(SAME))
											.collect(DeparturesConsumer::new, DeparturesConsumer::accept, DeparturesConsumer::combine);
			logger.info("CONSUMER : " + konsument.message.toString());
			if (konsument.message != null && !konsument.message.toString().equals("")) {
				message += konsument.message.toString();
				Server.broadcastMessage(message);
			}
			departuresArray.clear();
		}
		departuresArray.addAll(currentList);
	}
}
