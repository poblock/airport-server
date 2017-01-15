package pl.airport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
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
		String message = "";
		if(arrivalsLastTime!=null) {
			message+="T;"+arrivalsLastTime.toString()+END;
		}
		synchronized(arrivalsArray) {
			for(Lot lot : arrivalsArray) {
				message += "A;N;"+lot.getEncodedString()+END;
			}
		}
		if(departuresLastTime!=null) {
			message+="T;"+departuresLastTime.toString()+END;
		}
		synchronized(departuresArray) {
			for(Lot lot : departuresArray) {
				message += "D;N;"+lot.getEncodedString()+END;
			}
		}
		logger.info("ALL FLIGHTS LENGTH : "+message.length());
		return message;
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
			logger.info("Comparing took "+result+" ms. Seconds : "+(result/1000));
			logger.info(wyniki.toString());	
			
			String message = "";
			if(arrivalsLastTime!=null) {
				message+="T;"+arrivalsLastTime.toString()+END;
			}
			WynikiConsumer konsument = wyniki.entrySet().stream().filter(item -> !item.getKey().equals(SAME)).
			collect(WynikiConsumer::new, WynikiConsumer::accept, WynikiConsumer::combine);
			logger.info("CONSUMER : "+konsument.message);
			Server.broadcastMessage(message);
			arrivalsArray.clear();
		} 
		arrivalsArray.addAll(currentList);
	}
	
	class WynikiConsumer implements Consumer<Entry<Character, ArrayList<Lot>>> {
		private String message = "";
		
		@Override
		public void accept(Entry<Character, ArrayList<Lot>> entry) {
			for(Lot lot : entry.getValue()) {
				logger.info(entry.getKey()+";"+lot.getEncodedString());
				message += "A;"+entry.getKey()+";"+lot.getEncodedString()+END;	 
			}
		}
		
		public void combine(WynikiConsumer other) {
			message += other.message;
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
			logger.info("Comparing took "+result+" ms. Seconds : "+(result/1000));
			logger.info(wyniki.toString());	
			wyniki.entrySet().stream().filter(item -> !item.getKey().equals(SAME)).forEach(entry -> {
				for(Lot lot : entry.getValue()) {
					logger.info(entry.getKey()+";"+lot.getEncodedString());
					String message = "";
					if(departuresLastTime!=null) {
						message+="T;"+departuresLastTime.toString()+END;
					}
					message += "D;"+entry.getKey()+";"+lot.getEncodedString();
					Server.broadcastMessage(message); 
				}
			});
			departuresArray.clear();
		}
		departuresArray.addAll(currentList);
	}
}
