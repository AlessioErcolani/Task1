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

}
