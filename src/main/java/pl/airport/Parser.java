package pl.airport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser {
	
	final Logger logger = LoggerFactory.getLogger(Parser.class);
	private LocalDateTime arrivalsLastTime = null;
	private LocalDateTime departuresLastTime = null;
	public static final int ARRIVALS = 0;
	public static final int DEPARTURES = 1;
	private HashMap<String,String> airportCodes;
	private String updatedText = "Updated:";
	
	public Parser() {
		loadAirports();
	}
	
	private void loadAirports() {
		URL url = Parser.class.getClassLoader().getResource("airports.txt");
        InputStream is = null;
        BufferedReader br = null;
        try {
        	is = url.openStream();
            br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuilder mock19 = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
            	mock19.append(line);
            }
            JSONArray arr = new JSONArray(mock19.toString()); 
            airportCodes = new HashMap<>();
            for(int i=0; i<arr.length(); i++) {
            	JSONArray airport = arr.getJSONArray(i);
            	String name = airport.getString(0);
            	String code = airport.getString(1);
            	airportCodes.put(name,code);
            }
            if(br!=null) br.close();
            if(is!=null) is.close();
        } catch(Exception e) {
        	logger.error("Error at loading airport list file : "+e.getMessage());
        }
        
        if(airportCodes!=null) {
        	logger.info("Loaded airports : "+airportCodes.toString());
        }
	}
	
	private boolean isDataRefreshed(String info, int table) throws ParseException {
		if(info!=null) {
			logger.info("INFO :"+info);
			int index = info.indexOf(updatedText);
			
			if(index!=-1) {
				int startIdxDate = index - 11;
				int startIdxHour = index+updatedText.length()+1;
				
				String data = info.substring(startIdxDate, index).trim();
				String godzina = info.substring(startIdxHour);
				if(data!=null && godzina!=null) {
					SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy");
					f.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));
			    	SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd");
			    	f2.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"));
			    	String d2 = f2.format(f.parse(data));
					if(d2!=null) {
						LocalDateTime czas = LocalDateTime.parse(d2+"T"+godzina);
						if(czas!=null) {
							logger.info("Czas: "+czas.toString());
							switch (table) {
							case ARRIVALS:
								if(arrivalsLastTime!=null) System.out.println("Last arrivals: "+arrivalsLastTime.toString());
								if(arrivalsLastTime==null || (arrivalsLastTime!=null && czas.isAfter(arrivalsLastTime))) {
									arrivalsLastTime = czas;
									logger.info("ARRIVALS UPDATE");
									return true;
								} 
								break;
							case DEPARTURES:
								if(departuresLastTime!=null) System.out.println("Last departures: "+departuresLastTime.toString());
								if(departuresLastTime==null || (departuresLastTime!=null && czas.isAfter(departuresLastTime))) {
									departuresLastTime = czas;
									logger.info("DEPARTURES UPDATE");
									return true;
								} 
								break;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public List<Lot> parse(String url, int table) throws Exception {
		Document doc = Jsoup.connect(url).get();
		if(doc!=null) {
			Element lastUpdate = doc.select(".bgBlue").first();
			if(lastUpdate!=null) {
				if(isDataRefreshed(lastUpdate.text(), table)) {
					Element lotyTable = doc.select("section.bxTimeTable").first();
					Elements wiersze = lotyTable.select("tr");
					ListIterator<Element> it = wiersze.listIterator();
					ArrayList<Lot> flightsArray = new ArrayList<Lot>();
					boolean nextDay = false;
					while(it.hasNext()) {
						Element el = it.next();
						Elements elementy = el.select("td");
						ListIterator<Element> list = elementy.listIterator();
						boolean firstTime = true;
						Lot lot = new Lot();
						while(list.hasNext()) {
							Element kolumna = list.next();
							String klasa = kolumna.className().trim();
							if(!klasa.equals("logo")) {
								String value = kolumna.text();
								if(klasa.equals("airport")) {
									lot.setAirport(changeAirportName(value));
								} else if(klasa.equals("flight")) {
									String cleanValue = value.replace(" ",""); 
									if(cleanValue!=null) {
										lot.setFlight(cleanValue);
									}
								} else if(klasa.equals("status") || klasa.equals("status red")) {
									lot.setStatus(value);
								} else if(klasa.equals("time")) {
									if(firstTime) {
										lot.setTime(value);
										firstTime = false;
									} else {
										lot.setTimeExp(value);
										firstTime = true;
									}
								} else {
									if(klasa.equals("nextDay")) {
										nextDay = true;
									}
								}
							} else {
								for(Node n : kolumna.childNodes()) {
									if(n.hasAttr("alt")) {
										String linia = n.attr("alt");
										if(linia!=null) {
											lot.setAirline(changeAirlineName(linia));
											break;
										}
									}
								}
							}
						}
						if(lot.getAirport()!=null && lot.getFlight()!=null) {
							lot.setBiezacyDzien(!nextDay);
							flightsArray.add(lot);
						}
					}
					logger.info(flightsArray.toString());
					return flightsArray;
				}
			}
		}
		return null;
	}
	
	private String changeAirportName(String airport) {
		String[] unwantedCharacters = {" - ", "-", "\\", "/"};
		for(String c : unwantedCharacters) {
			if(airport.contains(c)) {
				airport = airport.replace(c, " ");
			}
		}
		String kod = airportCodes.get(airport.toUpperCase());
		if(kod!=null) {
			return kod;
		} else {
			logger.warn("ERROR! No code for "+airport+", return: "+airport.toUpperCase());
		}
		return airport.toUpperCase();
	}
	
	private String changeAirlineName(String airline) {
		if(airline.equals("Wizz Air")) {
			airline = "Wizzair";
		} else if(airline.equals("Polskie Linie Lotnicze LOT")) {
			airline = "LOT";
		} else if(airline.equals("SAS Scandinavian Airlines")) {
			airline = "SAS";
		}
		return airline;
	}
}
