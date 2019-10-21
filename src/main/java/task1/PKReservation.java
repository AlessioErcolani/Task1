package task1;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Embeddable;

@Embeddable
public class PKReservation implements Serializable {

	private static final long serialVersionUID = 1L;

	private LocalDate checkInDate;
	private Room room;

	public PKReservation(LocalDate checkInDate, Room room) {
		this.setCheckInDate(checkInDate);
		this.setRoom(room);
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public LocalDate getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(LocalDate checkInDate) {
		this.checkInDate = checkInDate;
	}

}
