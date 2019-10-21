package task1;

import java.time.LocalDate;
import javax.persistence.*;

@Entity(name = "Reservation")
@Table(name = "reservation")
@IdClass(PKReservation.class)
public class Reservation {
	@ManyToOne
	@JoinColumn(name = "ID_costumer")
	private Customer customer;

	@Id
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "ID_hotel", referencedColumnName = "ID_hotel"),
			@JoinColumn(name = "ID_room", referencedColumnName = "room_number") })
	private Room room;

	@Id
	private LocalDate checkInDate;
	private LocalDate checkOutDate;

	public Reservation() {

	}

	public Reservation(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
		this.room = room;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
	}

	public LocalDate getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(LocalDate checkInDate) {
		this.checkInDate = checkInDate;
	}

	public LocalDate getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(LocalDate checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}
