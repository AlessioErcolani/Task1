package task1;

import java.util.Date;

import javax.persistence.*;

@Entity(name = "Reservation")
@Table(name = "reservation", 
		uniqueConstraints =  @UniqueConstraint(
			name = "uk_room_checkIn",
			columnNames = {
					"room_id",		
					"check_in"
			}
		))
@NamedQuery(
		name="Reservation.getByHotelAndRoomAndCheckInDate",
		query="SELECT r FROM Reservation r WHERE r.room.hotel.id = :hotelId AND r.room.number = :roomNumber AND r.checkInDate = :checkInDate") 
@NamedQuery(
		name="Reservation.getByCustomer",
		query="SELECT r FROM Reservation r WHERE r.customer.id = :customerId AND r.checkInDate >= current_time") 
@NamedQuery(
		name="Reservation.getByHotel",
		query="SELECT r FROM Reservation r WHERE r.room.hotel.id = :hotelId AND r.checkInDate >= :from")
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "room_id", referencedColumnName = "id")
	private Room room;
	
	@ManyToOne
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
	private Customer customer;

	@Column(name = "check_in")
	@Temporal(TemporalType.DATE)
	private Date checkInDate;
	
	@Column(name = "check_out")
	@Temporal(TemporalType.DATE)
	private Date checkOutDate;

	public Reservation(Date checkInDate, Date checkOutDate) {
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
	}
	
	public Reservation(Room room, Date checkInDate, Date checkOutDate) {
		this.room = room;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
	}
	
	public Reservation(Room room, Date checkInDate, Date checkOutDate, Customer customer) {
		this.room = room;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.customer = customer;
	}

	public Reservation() {

	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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