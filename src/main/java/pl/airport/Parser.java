package pl.airport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

import org.joda.time.LocalDateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class Parser {
	
	private LocalDateTime arrivalsLastTime = null;
	private LocalDateTime departuresLastTime = null;
	public static final int ARRIVALS = 0;
	public static final int DEPARTURES = 1;
	private HashMap<String,String> airportCodes;
	
	public Parser() {
		airportCodes = new HashMap<>();
		airportCodes.put("ABERDEEN","ABZ");
		airportCodes.put("ALESUND","AES");
		airportCodes.put("ALICANTE","ALC");
		airportCodes.put("AMSTERDAM","AMS");
		airportCodes.put("BARCELONA","BCN");
		airportCodes.put("BELFAST","BFS");
		airportCodes.put("BERGEN","BGO");
		airportCodes.put("BILLUND","BLL");
		airportCodes.put("BIRMINGHAM","BHX");
		airportCodes.put("BRISTOL","BRS");
		airportCodes.put("BRUKSELA CHARLEROI","CRL");
		airportCodes.put("CORK","ORK");
		airportCodes.put("DONCASTER SHEFFIELD","DSA");
		airportCodes.put("DORTMUND","DTM");
		airportCodes.put("DUBLIN","DUB");
		airportCodes.put("EDYNBURG","EDI");
		airportCodes.put("EINDHOVEN","EIN");
		airportCodes.put("FRANKFURT","FRA");
		airportCodes.put("FRANKFURT HAHN","HHN");
		airportCodes.put("GLASGOW","GLA");
		airportCodes.put("GOTEBORG","GSE");
		airportCodes.put("GRENOBLE","GNB");
		airportCodes.put("GRONINGEN","GRQ");
		airportCodes.put("HAMBURG","HAM");
		airportCodes.put("HAUGESUND","HAU");
		airportCodes.put("HELSINKI","HEL");
		airportCodes.put("KIJÓW ¯ULANY","IEV");
		airportCodes.put("KOLONIA BONN","CGN");
		airportCodes.put("KOPENHAGA","CPH");
		airportCodes.put("KRAKÓW","KRK");
		airportCodes.put("KRISTIANSAND","KRS");
		airportCodes.put("LEEDS","LBA");
		airportCodes.put("LIVERPOOL","LPL");
		airportCodes.put("LONDYN LUTON","LTN");
		airportCodes.put("LONDYN STANSTED","STN");
		airportCodes.put("MALMÖ","MMX");
		airportCodes.put("MALTA","MLA");
		airportCodes.put("MANCHESTER","MAN");
		airportCodes.put("MEDIOLAN","BGY");
		airportCodes.put("MOLDE","MOL");
		airportCodes.put("MONACHIUM","MUC");
		airportCodes.put("NEAPOL","NAP");
		airportCodes.put("NEWCASTLE","NCL");
		airportCodes.put("OSLO","OSL");
		airportCodes.put("OSLO TORP","TRF");
		airportCodes.put("OSLO RYGGE","RYG");
		airportCodes.put("PARY¯ BEAUVAIS","BVA");
		airportCodes.put("PIZA","PSA");
		airportCodes.put("RADOM","RDO");
		airportCodes.put("REYKJAVÍK","KEF");
		airportCodes.put("STAVANGER","SVG");
		airportCodes.put("SZTOKHOLM ARLANDA","ARN");
		airportCodes.put("SZTOKHOLM SKAVSTA","NYO");
		airportCodes.put("TRONDHEIM","TRD");
		airportCodes.put("TURKU","TKU");
		airportCodes.put("VAXJO","VXO");
		airportCodes.put("WARSZAWA","WAW");
	}
	
	public static void main(String[] args) {
		try {
			Parser parser = new Parser();
			parser.parse("http://www.airport.gdansk.pl/schedule/arrivals-table", ARRIVALS);
			parser.parse("http://www.airport.gdansk.pl/schedule/departures-table", DEPARTURES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isDataRefreshed(String info, int table) throws ParseException {
		if(info!=null) {
			System.out.println(info);
			String search = "Aktualizacja:";
			int index = info.indexOf(search);
			if(index!=-1) {
				int startIdxDate = index - 11;
				int startIdxHour = index+search.length()+1;
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
							System.out.println("Czas: "+czas.toString());
							switch (table) {
							case ARRIVALS:
								if(arrivalsLastTime!=null) System.out.println("Last arrivals: "+arrivalsLastTime.toString());
								if(arrivalsLastTime==null || (arrivalsLastTime!=null && czas.isAfter(arrivalsLastTime))) {
									arrivalsLastTime = czas;
									System.out.println("ARRIVALS UPDATE");
									return true;
								} 
								break;
							case DEPARTURES:
								if(departuresLastTime!=null) System.out.println("Last departures: "+departuresLastTime.toString());
								if(departuresLastTime==null || (departuresLastTime!=null && czas.isAfter(departuresLastTime))) {
									departuresLastTime = czas;
									System.out.println("DEPARTURES UPDATE");
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
								} else if(klasa.equals("status")) {
									lot.setStatus(value);
								} else if(klasa.equals("time")) {
									if(firstTime) {
										lot.setTime(value);
										firstTime = false;
									} else {
										lot.setTimeExp(value);
										firstTime = true;
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
							flightsArray.add(lot);
						}
					}
					System.out.println(flightsArray);
					return flightsArray;
				}
			}
		}
		return null;
	}
	
	private String changeAirportName(String airport) {
		if(airport.contains(" - ")) {
			airport = airport.replace(" - ", " ");
		} else if(airport.contains("-")) {
			airport = airport.replace("-", " ");
		}
		String kod = airportCodes.get(airport.toUpperCase());
		if(kod!=null) {
			return kod;
		} else {
			System.out.println("B£¥D!! Nie ma kodu dla "+airport+" "+airport.toUpperCase());
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
