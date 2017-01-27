package pl.airport.parser;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pl.airport.flights.Arrivals;
import pl.airport.flights.Commons;
import pl.airport.flights.Departures;
import pl.airport.model.Lot;

public class Scheduler extends Timer {
	private Parser parser = null;
	private String arrivalsURL;
	private String departuresURL;
	private Arrivals arrivals = null;
	private Departures departures = null;
	
	public Scheduler() {
		super(true);
		parser = new Parser();
		arrivals = new Arrivals();
		departures = new Departures();
		arrivalsURL = "http://www.airport.gdansk.pl/en/schedule/arrivals-table";
		departuresURL = "http://www.airport.gdansk.pl/en/schedule/departures-table";
		scheduleAtFixedRate(new Task(), 0, 60*1000);
	}
	
	class Task extends TimerTask {

		@Override
		public void run() {
			try {
				List<Lot> przyloty = parser.parse(arrivalsURL, Commons.ARRIVALS);
				List<Lot> odloty = parser.parse(departuresURL, Commons.DEPARTURES);
				arrivals.checkArrivals(przyloty);
				departures.checkDepartures(odloty);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}