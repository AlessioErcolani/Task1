package task1;

import java.util.*;
import javax.persistence.*;

@Entity(name = "Hotel")
@Table(name = "hotel")
public class Hotel {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long ID_hotel;
	private String city;
	private String street;
	private int number;
	
	@OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Room> rooms = new ArrayList<Room>();
	
	@OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Receptionist> receptionists = new ArrayList<Receptionist>();

	public Long getID_hotel() {
		return ID_hotel;
	}

	public void setID_hotel(Long iD_hotel) {
		ID_hotel = iD_hotel;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
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
	

}
