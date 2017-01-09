package pl.airport;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler extends Timer {
	private Parser parser = null;
	public Scheduler() {
		super(true);
		parser = new Parser();
		scheduleAtFixedRate(new Task(), 0, 60*1000);
	}
	
	class Task extends TimerTask {

		@Override
		public void run() {
			try {
				List<Lot> przyloty = parser.parse("http://www.airport.gdansk.pl/schedule/arrivals-table", Parser.ARRIVALS);
				List<Lot> odloty = parser.parse("http://www.airport.gdansk.pl/schedule/departures-table", Parser.DEPARTURES);
				Flights.checkFlights(przyloty, odloty);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}