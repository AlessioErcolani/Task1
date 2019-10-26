package task1;

import java.util.Date;

import javax.persistence.*;

@Entity(name = "Reservation")
@Table(name = "reservation")
@IdClass(PKReservation.class)
@NamedQuery(
		name="Reservation.getByHoteAndRoomAndCheckInDate",
		query="SELECT r FROM Reservation r WHERE r.room.hotel.hotelId = :hotelId AND r.room.roomNumber = :roomNumber AND r.checkInDate = :checkInDate")
@NamedQuery(
		name="Reservation.getByCustomer",
		query="SELECT r FROM Reservation r WHERE r.customer.ID = :customerId AND r.checkInDate >= current_time")
@NamedQuery(
		name="Reservation.getByHotel",
		query="SELECT r FROM Reservation r WHERE r.room.hotel.hotelId = :hotelId AND r.checkInDate >= :from")
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
	@Temporal(TemporalType.DATE)
	private Date checkInDate;
	
	@Temporal(TemporalType.DATE)
	private Date checkOutDate;

	public Reservation() {

	}

	public Reservation(Room room, Date checkInDate, Date checkOutDate) {
		this.room = room;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
	}

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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((checkInDate == null) ? 0 : checkInDate.hashCode());
		result = prime * result + ((checkOutDate == null) ? 0 : checkOutDate.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((room == null) ? 0 : room.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Reservation [customer=" + customer + ", room=" + room + ", checkInDate=" + checkInDate
				+ ", checkOutDate=" + checkOutDate + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reservation other = (Reservation) obj;
		
		if (checkInDate == null) {
			if (other.checkInDate != null)
				return false;
		} else if (!checkInDate.equals(other.checkInDate)) {
			return false;
		}

		if (checkOutDate == null) {
			if (other.checkOutDate != null)
				return false;
		} else if (!checkOutDate.equals(other.checkOutDate)) {
			return false;
		}

		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer)) {
			return false;
		}
		
		if (room == null) {
			if (other.room != null)
				return false;
		} else if (!room.equals(other.room)) {
			return false;
		}
		return true;
	}

	
}
