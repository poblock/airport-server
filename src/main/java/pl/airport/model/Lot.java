package pl.airport.model;

public class Lot {
	private String airport;
	private String flight;
	private String airline;
	private String time;
	private String timeExp;
	private String status;
	private boolean biezacyDzien;
	
	public Lot() {

	}

	public Lot(String airport, String flight, String airline, String time, String timeExp, String status,
			boolean biezacyDzien) {
		this.airport = airport;
		this.flight = flight;
		this.airline = airline;
		this.time = time;
		this.timeExp = timeExp;
		this.status = status;
		this.biezacyDzien = biezacyDzien;
	}

	public String getAirport() {
		return airport;
	}

	public void setAirport(String airport) {
		this.airport = airport;
	}

	public String getFlight() {
		return flight;
	}

	public void setFlight(String flight) {
		this.flight = flight;
	}

	public String getAirline() {
		return airline;
	}

	public void setAirline(String airline) {
		this.airline = airline;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTimeExp() {
		return timeExp;
	}

	public void setTimeExp(String timeExp) {
		this.timeExp = timeExp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isBiezacyDzien() {
		return biezacyDzien;
	}

	public void setBiezacyDzien(boolean biezacyDzien) {
		this.biezacyDzien = biezacyDzien;
	}
	
	public String getID() {
		return getAirport()+";"+getFlight()+";"+isBiezacyDzien();
	}
	
	public String getEncodedString() {
		return getID()+";"+getAirline()+";"+getTime()+";"+getTimeExp().trim()+";"+getStatus().trim();
	}

	@Override
	public String toString() {
		return "Lot [airport=" + airport + ", flight=" + flight + ", airline=" + airline + ", time=" + time
				+ ", timeExp=" + timeExp + ", status=" + status + ", biezacyDzien=" + biezacyDzien + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((airline == null) ? 0 : airline.hashCode());
		result = prime * result + ((airport == null) ? 0 : airport.hashCode());
		result = prime * result + (biezacyDzien ? 1231 : 1237);
		result = prime * result + ((flight == null) ? 0 : flight.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((timeExp == null) ? 0 : timeExp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lot other = (Lot) obj;
		if (airline == null) {
			if (other.airline != null)
				return false;
		} else if (!airline.equals(other.airline))
			return false;
		if (airport == null) {
			if (other.airport != null)
				return false;
		} else if (!airport.equals(other.airport))
			return false;
		if (biezacyDzien != other.biezacyDzien)
			return false;
		if (flight == null) {
			if (other.flight != null)
				return false;
		} else if (!flight.equals(other.flight))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (timeExp == null) {
			if (other.timeExp != null)
				return false;
		} else if (!timeExp.equals(other.timeExp))
			return false;
		return true;
	}
}
