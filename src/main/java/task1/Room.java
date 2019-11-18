package task1;

import java.util.*;
import javax.persistence.*;

@Entity(name = "Room")
@Table(name = "room", 
	   uniqueConstraints =  @UniqueConstraint(
			name = "uk_hotel_number",
        	columnNames = {
    			"hotel_id",		
    			"number"
        	}
	   ))
@NamedQuery(
		name="Room.findByHotel",
		query="SELECT r FROM Room r WHERE r.hotel.id = :hotelId") 
@NamedQuery(
		name="Room.findByHotelAndNumber",
		query="SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.number = :roomNumber")
@NamedQuery(
		name="Room.getReservableRoomsGivenPeriod", 
		query=""
				+ "SELECT r "
				+ "FROM Room r "
				+ "WHERE r.hotel.id = :hotelId AND r.available = true AND r.id NOT IN "
				+ "("
				+ "		SELECT res.room.id "
				+ "		FROM Reservation res "
				+ "		WHERE res.room.hotel.id = :hotelId "
				+ "			AND (((:startPeriod <= res.checkInDate) AND (:endPeriod >= res.checkOutDate)) "
				+ "			OR ((:startPeriod <= res.checkInDate) AND (:endPeriod > res.checkInDate)) "
				+ "			OR ((:startPeriod < res.checkOutDate) AND (:endPeriod >= res.checkOutDate)) "
				+ "			OR ((:startPeriod > res.checkInDate) AND (:endPeriod < res.checkOutDate))) "
				+ ")") 		
@NamedQuery(
		name="Room.getUnreservableRoomsGivenPeriod",
		query=""
				+ "SELECT r "
				+ "FROM Room r "
				+ "WHERE r.hotel.id = :hotelId AND "
				+ "( "
				+ "		(r.available = false) "
				+ "		OR "
				+ "		(r.id IN "
				+ "			(SELECT res.room.id "
				+ "			FROM Reservation res "
				+ "			  	WHERE res.room.hotel.id = :hotelId "
				+ "					AND ((:startPeriod <= res.checkInDate AND :endPeriod >= res.checkOutDate) " 
				+ "					OR (:startPeriod <= res.checkInDate AND :endPeriod > res.checkInDate) "  
				+ "					OR (:startPeriod < res.checkOutDate AND :endPeriod >= res.checkOutDate) " 
				+ "					OR (:startPeriod > res.checkInDate AND :endPeriod < res.checkOutDate)) "
				+ " 		)"	
				+ "		)"
				+ ")") 
public class Room {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "hotel_id", referencedColumnName = "id")
	private Hotel hotel;

	@Column(name = "number")
	private int number;

	@Column(name = "capacity")
	private int capacity;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
	List<Reservation> reservations = new ArrayList<Reservation>();

	private boolean available;

	public Room(int roomNumber, int roomCapacity, Hotel hotel, boolean available) {
		this.number = roomNumber;
		this.capacity = roomCapacity;
		this.hotel = hotel;
		this.available = available;
	}
	
	public Room(int roomNumber, int roomCapacity, Hotel hotel) {
		this(roomNumber, roomCapacity, hotel, true);
	}
	
	public Room(int roomNumber, int roomCapacity, boolean available) {
		this.number = roomNumber;
		this.capacity = roomCapacity;
		this.available = available;
	}
	
	public Room(int roomNumber, int roomCapacity) {
		this(roomNumber, roomCapacity, true);
	}

	public Room() {

	}
	
	public void addReservation(Reservation reservation) {
		reservations.add(reservation);
		reservation.setRoom(this);
	}
	
	public void removeReservation(Reservation reservation) {
		reservations.remove(reservation);
		reservation.setRoom(null);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Hotel getHotel() {
		return hotel;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (available ? 1231 : 1237);
		result = prime * result + ((hotel == null) ? 0 : hotel.hashCode());
		result = prime * result + capacity;
		result = prime * result + number;
		return result;
	}
	
	@Override
	public String toString() {
		return "Room [hotel=" + hotel + ", roomNumber=" + number + ", roomCapacity=" + capacity + ", available="
				+ available + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		if (available != other.available)
			return false;
		if (hotel == null) {
			if (other.hotel != null)
				return false;
		} else if (!hotel.equals(other.hotel))
			return false;
		if (capacity != other.capacity)
			return false;
		if (number != other.number)
			return false;
		return true;
	}
}
