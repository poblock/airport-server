package pl.airport;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler extends Timer {
	private Parser parser = null;
	private String arrivalsURL;
	private String departuresURL;
	
	public Scheduler() {
		super(true);
		parser = new Parser();
		arrivalsURL = "http://www.airport.gdansk.pl/en/schedule/arrivals-table";
		departuresURL = "http://www.airport.gdansk.pl/en/schedule/departures-table";
		scheduleAtFixedRate(new Task(), 0, 60*1000);
	}
	
	class Task extends TimerTask {

		@Override
		public void run() {
			try {
				List<Lot> przyloty = parser.parse(arrivalsURL, Parser.ARRIVALS);
				List<Lot> odloty = parser.parse(departuresURL, Parser.DEPARTURES);
				Flights.checkFlights(przyloty, odloty);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}