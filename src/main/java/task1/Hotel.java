package task1;

import java.util.*;
import javax.persistence.*;

@Entity(name = "Hotel")
@Table(name = "hotel")
public class Hotel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_hotel")
	private Long hotelId;
	private String city;
	private String street;
	private int streetNumber;

	@OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Room> rooms = new ArrayList<Room>();

	@OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Receptionist> receptionists = new ArrayList<Receptionist>();

	public Hotel() {

	}

	public Hotel(String city, String street, int streetNumber) {
		this.city = city;
		this.street = street;
		this.streetNumber = streetNumber;
	}

	public Long getHotelId() {
		return hotelId;
	}

	public void setHotelId(Long hotelId) {
		this.hotelId = hotelId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(int streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
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
}
