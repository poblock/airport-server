package airport;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import pl.airport.Lot;
import pl.airport.Flights;

public class FlightsTest {

	HashMap<Character, ArrayList<Lot>> wyniki;
	
	@Before
	public void init() {
		wyniki = new HashMap<>();
		wyniki.put(Flights.UPDATE, new ArrayList<Lot>());
		wyniki.put(Flights.SAME, new ArrayList<Lot>());
		wyniki.put(Flights.NEW, new ArrayList<Lot>());
		wyniki.put(Flights.REMOVED, new ArrayList<Lot>());
		
		wyniki.get(Flights.UPDATE).add(new Lot("FRA", "LH1376", "Lufthansa", "13:55", "", "", true));
		wyniki.get(Flights.UPDATE).add(new Lot("CPH", "SK759", "SAS", "14:15", "", "", true));
		wyniki.get(Flights.UPDATE).add(new Lot("HAU", "W61750", "Wizzair", "14:25", "", "", true));
		wyniki.get(Flights.UPDATE).add(new Lot("WAW", "LO3837", "LOT", "14:30", "", "", true));
		wyniki.get(Flights.UPDATE).add(new Lot("MMX", "W61740", "Wizzair", "14:40", "", "", true));
		wyniki.get(Flights.UPDATE).add(new Lot("WAW", "FR3195", "Ryanair", "15:15", "", "", true));
		wyniki.get(Flights.UPDATE).add(new Lot("MUC", "LH1644", "Lufthansa", "16:20", "", "", true));
		wyniki.get(Flights.UPDATE).add(new Lot("EDI", "FR7921", "Ryanair", "17:15", "", "", true));
		wyniki.get(Flights.UPDATE).add(new Lot("WAW", "LO3815", "LOT", "17:35", "", "", true));
		wyniki.get(Flights.UPDATE).add(new Lot("HEL", "AY751", "Finnair", "17:45", "", "", true));

		wyniki.get(Flights.SAME).add(new Lot("CPH", "SK757", "SAS", "18:05", "", "", true));
		wyniki.get(Flights.SAME).add(new Lot("KRK", "FR7151", "Ryanair", "18:15", "", "", true));
		wyniki.get(Flights.SAME).add(new Lot("TFS", "TVS7317", "Travel Service", "18:15", "", "", true));
		wyniki.get(Flights.SAME).add(new Lot("BGO", "W61746", "Wizzair", "18:15", "", "", true));
		wyniki.get(Flights.SAME).add(new Lot("MLA", "FR5207", "Ryanair", "19:35", "", "", true));
		wyniki.get(Flights.SAME).add(new Lot("KRK", "LO3502", "LOT", "19:50", "19:40", "", true));
		wyniki.get(Flights.SAME).add(new Lot("WAW", "LO3825", "LOT", "20:35", "", "", true));
		wyniki.get(Flights.SAME).add(new Lot("MUC", "LH1646", "Lufthansa", "20:40", "", "", true));
		wyniki.get(Flights.SAME).add(new Lot("WAW", "FR3099", "Ryanair", "20:55", "", "", true));

		wyniki.get(Flights.NEW).add(new Lot("STN", "FR2372", "Ryanair", "21:45", "", "", true));
		wyniki.get(Flights.NEW).add(new Lot("FRA", "LH1378", "Lufthansa", "23:05", "", "", true));
		wyniki.get(Flights.NEW).add(new Lot("AES", "W61766", "Wizzair", "23:10", "", "", true));
		wyniki.get(Flights.NEW).add(new Lot("WAW", "LO3827", "LOT", "23:35", "", "", true));
		wyniki.get(Flights.NEW).add(new Lot("LTN", "W61608", "Wizzair", "23:40", "", "", true));
		wyniki.get(Flights.NEW).add(new Lot("CPH", "SK753", "SAS", "23:50", "", "", true));
		wyniki.get(Flights.NEW).add(new Lot("DSA", "W61616", "Wizzair", "23:55", "", "", true)); 

		wyniki.get(Flights.REMOVED).add(new Lot("LTN", "W61602", "Wizzair", "11:45", "12:10", "LANDED", true));
		wyniki.get(Flights.REMOVED).add(new Lot("STN", "FR2374", "Ryanair", "12:35", "12:48", "DELAYED 12:48", true));
		wyniki.get(Flights.REMOVED).add(new Lot("MUC", "LH1642", "Lufthansa", "12:40", "12:38", "", true));
		wyniki.get(Flights.REMOVED).add(new Lot("NYO", "FR8320", "Ryanair", "13:20", "13:11", "", true));
		wyniki.get(Flights.REMOVED).add(new Lot("TKU", "W61752", "Wizzair", "13:20", "13:29", "", true));
		wyniki.get(Flights.REMOVED).add(new Lot("BGY", "FR8312", "Ryanair", "13:45", "14:00", "", true)); 
		wyniki.get(Flights.REMOVED).add(new Lot("WAW", "LO3835", "LOT", "11:40", "11:34", "LANDED", true));
	}
	
	@Test
	public void showWyniki() {
		assertNotNull(wyniki);	
		WynikiConsumer konsument = wyniki.entrySet().stream().filter(item -> !item.getKey().equals(Flights.SAME)).
				collect(WynikiConsumer::new, WynikiConsumer::accept, WynikiConsumer::combine);
		System.out.println(konsument.message);
	}
	
	class WynikiConsumer implements Consumer<Entry<Character, ArrayList<Lot>>> {
		private String message = "";
		
		@Override
		public void accept(Entry<Character, ArrayList<Lot>> entry) {
			for(Lot lot : entry.getValue()) {
				message += "A;"+entry.getKey()+";"+lot.getEncodedString()+Flights.END;	 
			}
		}
		
		public void combine(WynikiConsumer other) {
			message += other.message;
		}
	}
}
