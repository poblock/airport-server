package pl.airport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Flights {
	static List<Lot> arrivalsArray = Collections.synchronizedList(new ArrayList<Lot>());
	static List<Lot> departuresArray = Collections.synchronizedList(new ArrayList<Lot>());

	public static void checkFlights(List<Lot> arrivals, List<Lot> departures) {
		if (arrivals != null) {
			System.out.println("Compare Arrivals");
			compareLists(arrivals, arrivalsArray);
			arrivalsArray = arrivals;
		}
		if (departures != null) {
			System.out.println("Compare Departures");
			compareLists(departures, departuresArray);
			departuresArray = departures;
		}
	}

	public static HashMap<String, Lot> compareLists(List<Lot> currentList, List<Lot> lastList) {
		HashMap<String, Lot> wyniki = new HashMap<>();
		if(currentList!=null && !currentList.isEmpty() &&
				lastList!=null && !lastList.isEmpty()) {
			long start = System.currentTimeMillis();
			HashMap<Lot, Boolean> used = new HashMap<>();
			for(Lot lot : lastList) {
				used.put(lot, false);
			}
			System.out.println("Current : "+currentList);
			System.out.println("Last : "+lastList);
			for(Lot curr : currentList) {
				Lot result = getFlight(curr, lastList);
				if(result!=null) {
					if(!result.equals(curr)) {
						wyniki.put("UPDATE", curr);
					} else {
						wyniki.put("BEZ ZMIAN", curr);
					}
					used.put(curr, true);
				} else {
					wyniki.put("NOWY", curr);
				}
			}
			
			Iterator<Lot> it = used.keySet().iterator();
			while(it.hasNext()) {
				Lot lot = it.next();
				if(!used.get(lot)) {
					wyniki.put("REMOVED", lot);
				}
			}
			long stop = System.currentTimeMillis();
			// Server.broadcastMessage("Serwer", currentList.toString());
			System.out.println(wyniki);
		}
		return wyniki;
	}
	
	private static Lot getFlight(Lot lot, List<Lot> list) {
		for(Lot l : list) {
			if(l.getAirport().equals(lot.getAirport()) && 
					l.getFlight().equals(lot.getFlight())) {
				return l;
			}
		}
		return null;
	}
}
