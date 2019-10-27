package task1;

import java.util.*;
import javax.persistence.*;

@Entity(name = "Room")
@Table(name = "room")
@IdClass(PKRoom.class)
@NamedQuery(
		name="Room.findByHotelAndNumber",
		query="SELECT r FROM Room r WHERE r.hotel.hotelId = :hotelId AND r.roomNumber = :roomNumber")
@NamedQuery(
		name="Room.getReservableRoomsGivenPeriod", 
		query=""
				+ "SELECT r "
				+ "FROM Room r "
				+ "WHERE r.hotel.hotelId = :hotelId AND r.available = true AND r.roomNumber NOT IN "
				+ "("
				+ "		SELECT res.room.roomNumber "
				+ "		FROM Reservation res "
				+ "		WHERE res.room.hotel.hotelId = :hotelId "
				+ "			AND ((:startPeriod < res.checkInDate AND :endPeriod > res.checkOutDate) "
				+ "			OR (:startPeriod < res.checkInDate AND :endPeriod > res.checkInDate) "
				+ "			OR (:startPeriod < res.checkOutDate AND :endPeriod > res.checkOutDate) "
				+ "			OR (:startPeriod > res.checkInDate AND :endPeriod < res.checkOutDate)) "
				+ ")")
@NamedQuery(
		name="Room.getUnreservableRoomsGivenPeriod",
		query=""
				+ "SELECT r "
				+ "FROM Room r "
				+ "WHERE r.hotel.hotelId = :hotelId AND "
				+ "( "
				+ "		(r.available = false) "
				+ "		OR "
				+ "		(r.roomNumber IN "
				+ "			(SELECT res.room.roomNumber "
				+ "			FROM Reservation res "
				+ "			  	WHERE res.room.hotel.hotelId = :hotelId "
				+ "					AND ((:startPeriod < res.checkInDate AND :endPeriod > res.checkOutDate) " 
				+ "					OR (:startPeriod < res.checkInDate AND :endPeriod > res.checkInDate) "  
				+ "					OR (:startPeriod < res.checkOutDate AND :endPeriod > res.checkOutDate) " 
				+ "					OR (:startPeriod > res.checkInDate AND :endPeriod < res.checkOutDate)) "
				+ " 		)"	
				+ "		)"
				+ ")")
public class Room {
	@Id
	@ManyToOne
	@JoinColumn(name = "ID_hotel")
	private Hotel hotel;

	@Id
	@Column(name = "room_number")
	private int roomNumber;

	@Column(name = "room_capacity")
	private int roomCapacity;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
	List<Reservation> reservations = new ArrayList<Reservation>();

	private boolean available;

	public Room() {

	}

	public Room(int roomNumber, int roomCapacity) {
		this(roomNumber, roomCapacity, true);
	}

	public Room(int roomNumber, int roomCapacity, boolean available) {
		this.roomNumber = roomNumber;
		this.roomCapacity = roomCapacity;
		this.available = available;
	}
	
	public void addReservation(Reservation reservation) {
		reservations.add(reservation);
		reservation.setRoom(this);
	}
	
	public void removeReservation(Reservation reservation) {
		reservations.remove(reservation);
		reservation.setRoom(null);
	}

	public int getRoomCapacity() {
		return roomCapacity;
	}

	public void setRoomCapacity(int roomCapacity) {
		this.roomCapacity = roomCapacity;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

	public int getRoomNumber() {
		return roomNumber;
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
	public String toString() {
		return "Room [hotel=" + hotel + ", roomNumber=" + roomNumber + ", roomCapacity=" + roomCapacity + ", available="
				+ available + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (available ? 1231 : 1237);
		result = prime * result + ((hotel == null) ? 0 : hotel.hashCode());
		result = prime * result + roomCapacity;
		result = prime * result + roomNumber;
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
		Room other = (Room) obj;
		if (available != other.available)
			return false;
		if (hotel == null) {
			if (other.hotel != null)
				return false;
		} else if (!hotel.equals(other.hotel))
			return false;
		if (roomCapacity != other.roomCapacity)
			return false;
		if (roomNumber != other.roomNumber)
			return false;
		return true;
	}
}
