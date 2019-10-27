package task1;

import java.util.*;
import javax.persistence.*;

@Entity(name = "Hotel")
@Table(name = "hotel")
@NamedQuery(
		name="Hotel.findAll",
		query="SELECT h FROM Hotel h")
@NamedQuery(
		name="Hotel.findByAddress",
		query="SELECT h FROM Hotel h WHERE h.address = :address")
public class Hotel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_hotel")
	private Long hotelId;
	@Column(unique = true)
	private String address;

	@OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Room> rooms = new ArrayList<Room>();

	@OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Receptionist> receptionists = new ArrayList<Receptionist>();

	public Hotel() {

	}

	public Hotel(String address) {
		this.address = address;
	}

	public Long getHotelId() {
		return hotelId;
	}

	public void setHotelId(Long hotelId) {
		this.hotelId = hotelId;
	}
	
	public List<Receptionist> getReceptionists() {
		return receptionists;
	}

	public void setReceptionists(List<Receptionist> receptionists) {
		this.receptionists = receptionists;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public void addRoom(Room room) {
		rooms.add(room);
		room.setHotel(this);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((hotelId == null) ? 0 : hotelId.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Hotel [hotelId=" + hotelId + ", address=" + address + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hotel other = (Hotel) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (hotelId == null) {
			if (other.hotelId != null)
				return false;
		} else if (!hotelId.equals(other.hotelId))
			return false;
		return true;
	}
}
