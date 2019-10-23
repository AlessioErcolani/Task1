package task1;

import java.util.*;
import javax.persistence.*;

@Entity(name = "Room")
@Table(name = "room")
@IdClass(PKRoom.class)
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (available ? 1231 : 1237);
		result = prime * result + ((hotel == null) ? 0 : hotel.hashCode());
		result = prime * result + ((reservations == null) ? 0 : reservations.hashCode());
		result = prime * result + roomCapacity;
		result = prime * result + roomNumber;
		return result;
	}
	
	@Override
	public String toString() {
		return "Room [hotel=" + hotel + ", roomNumber=" + roomNumber + ", roomCapacity=" + roomCapacity + ", available="
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
		if (reservations == null) {
			if (other.reservations != null)
				return false;
		} else if (!reservations.equals(other.reservations))
			return false;
		if (roomCapacity != other.roomCapacity)
			return false;
		if (roomNumber != other.roomNumber)
			return false;
		return true;
	}

}
