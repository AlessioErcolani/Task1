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
	private Long roomNumber;

	@Column(name = "room_capacity")
	private int roomCapacity;
	
	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
	List<Reservation> reservations = new ArrayList<Reservation>();
	
	private boolean available;
	
	public int getRoomCapacity() {
		return roomCapacity;
	}
	public void setRoomCapacity(int roomCapacity) {
		this.roomCapacity = roomCapacity;
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}

}
