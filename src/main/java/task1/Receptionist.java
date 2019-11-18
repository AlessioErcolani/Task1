package task1;

import javax.persistence.*;

@Entity(name = "Receptionist")
@Table(name = "receptionist")
@NamedQuery(
		name="Receptionist.findByUsernameAndPassword",
		query="SELECT r FROM Receptionist r WHERE r.username = :username AND r.password = :password")
@NamedQuery(
		name="Receptionist.findByUsername",
		query="SELECT r FROM Receptionist r WHERE r.username = :username")
public class Receptionist extends User {
	@ManyToOne
	@JoinColumn(name = "hotel_id", referencedColumnName = "id")
	private Hotel hotel;
	
	public Receptionist(String username, String password, String name, String surname, Hotel hotel) {
		super(username, password, name, surname);
		this.hotel = hotel;
	}

	public Receptionist(String username) {
		super(username);
	}
	
	public Receptionist() {
		super();
	}
	
	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((hotel == null) ? 0 : hotel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Receptionist other = (Receptionist) obj;
		if (hotel == null) {
			if (other.hotel != null)
				return false;
		} else if (!hotel.equals(other.hotel))
			return false;
		return true;
	}
}