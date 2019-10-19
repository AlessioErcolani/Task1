package task1;

import java.util.*;

import javax.persistence.*;

@Entity(name = "Reservation")
@Table(name = "reservation")
@IdClass(PKReservation.class)
public class Reservation {
	@ManyToOne
	@JoinColumn(name  = "ID_costumer")
	private Customer customer;
	
	@Id
	@ManyToOne
	@JoinColumns({
        @JoinColumn(name = "ID_hotel", 	referencedColumnName = "ID_hotel"),
        @JoinColumn(name = "ID_room",		referencedColumnName = "room_number")
    })	
	private Room room;

	@Id
	private Date checkInDate;
	private Date checkOutDate;
	
	Reservation(){}
	
	public Date getCheckInDate() {
		return checkInDate;
	}
	public void setCheckInDate(Date checkInDate) {
		this.checkInDate = checkInDate;
	}
	public Date getCheckOutDate() {
		return checkOutDate;
	}
	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

}
