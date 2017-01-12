package airport;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import pl.airport.Lot;

public class FlightsTest {

	HashMap<String, ArrayList<Lot>> wyniki;
	
	@Before
	public void init() {
		wyniki = new HashMap<>();
		wyniki.put("UPDATE", new ArrayList<Lot>());
		wyniki.put("BEZ ZMIAN", new ArrayList<Lot>());
		wyniki.put("NOWY", new ArrayList<Lot>());
		wyniki.put("REMOVED", new ArrayList<Lot>());
		
//		wyniki.get("UPDATE").add(new Lot("FRA", "LH1376", "Lufthansa", "13:55", "", "", true));
//		wyniki.get("UPDATE").add(new Lot("CPH", "SK759", "SAS", "14:15", "", "", true));
//		wyniki.get("UPDATE").add(new Lot("HAU", "W61750", "Wizzair", "14:25", "", "", true));
//		wyniki.get("UPDATE").add(new Lot("WAW", "LO3837", "LOT", "14:30", "", "", true));
//		wyniki.get("UPDATE").add(new Lot("MMX", "W61740", "Wizzair", "14:40", "", "", true));
//		wyniki.get("UPDATE").add(new Lot("WAW", "FR3195", "Ryanair", "15:15", "", "", true));
//		wyniki.get("UPDATE").add(new Lot("MUC", "LH1644", "Lufthansa", "16:20", "", "", true));
//		wyniki.get("UPDATE").add(new Lot("EDI", "FR7921", "Ryanair", "17:15", "", "", true));
//		wyniki.get("UPDATE").add(new Lot("WAW", "LO3815", "LOT", "17:35", "", "", true));
//		wyniki.get("UPDATE").add(new Lot("HEL", "AY751", "Finnair", "17:45", "", "", true));

		wyniki.get("BEZ ZMIAN").add(new Lot("CPH", "SK757", "SAS", "18:05", "", "", true));
		wyniki.get("BEZ ZMIAN").add(new Lot("KRK", "FR7151", "Ryanair", "18:15", "", "", true));
		wyniki.get("BEZ ZMIAN").add(new Lot("TFS", "TVS7317", "Travel Service", "18:15", "", "", true));
		wyniki.get("BEZ ZMIAN").add(new Lot("BGO", "W61746", "Wizzair", "18:15", "", "", true));
		wyniki.get("BEZ ZMIAN").add(new Lot("MLA", "FR5207", "Ryanair", "19:35", "", "", true));
		wyniki.get("BEZ ZMIAN").add(new Lot("KRK", "LO3502", "LOT", "19:50", "19:40", "", true));
		wyniki.get("BEZ ZMIAN").add(new Lot("WAW", "LO3825", "LOT", "20:35", "", "", true));
		wyniki.get("BEZ ZMIAN").add(new Lot("MUC", "LH1646", "Lufthansa", "20:40", "", "", true));
		wyniki.get("BEZ ZMIAN").add(new Lot("WAW", "FR3099", "Ryanair", "20:55", "", "", true));

		wyniki.get("NOWY").add(new Lot("STN", "FR2372", "Ryanair", "21:45", "", "", true));
		wyniki.get("NOWY").add(new Lot("FRA", "LH1378", "Lufthansa", "23:05", "", "", true));
		wyniki.get("NOWY").add(new Lot("AES", "W61766", "Wizzair", "23:10", "", "", true));
		wyniki.get("NOWY").add(new Lot("WAW", "LO3827", "LOT", "23:35", "", "", true));
		wyniki.get("NOWY").add(new Lot("LTN", "W61608", "Wizzair", "23:40", "", "", true));
		wyniki.get("NOWY").add(new Lot("CPH", "SK753", "SAS", "23:50", "", "", true));
		wyniki.get("NOWY").add(new Lot("DSA", "W61616", "Wizzair", "23:55", "", "", true)); 

		wyniki.get("REMOVED").add(new Lot("LTN", "W61602", "Wizzair", "11:45", "12:10", "LANDED", true));
		wyniki.get("REMOVED").add(new Lot("STN", "FR2374", "Ryanair", "12:35", "12:48", "DELAYED 12:48", true));
		wyniki.get("REMOVED").add(new Lot("MUC", "LH1642", "Lufthansa", "12:40", "12:38", "", true));
		wyniki.get("REMOVED").add(new Lot("NYO", "FR8320", "Ryanair", "13:20", "13:11", "", true));
		wyniki.get("REMOVED").add(new Lot("TKU", "W61752", "Wizzair", "13:20", "13:29", "", true));
		wyniki.get("REMOVED").add(new Lot("BGY", "FR8312", "Ryanair", "13:45", "14:00", "", true)); 
		wyniki.get("REMOVED").add(new Lot("WAW", "LO3835", "LOT", "11:40", "11:34", "LANDED", true));
	}
	
	@Test
	public void showWyniki() {
		assertNotNull(wyniki);
		wyniki.entrySet().stream().filter(item -> !item.getKey().equals("BEZ ZMIAN")).forEach(entry -> {
//			for(Lot lot : entry.getValue()) {
//				System.out.println(lot);
//			}
			System.out.println(entry);
		});
	}
}
