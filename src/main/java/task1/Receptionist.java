package task1;

import javax.persistence.*;

@Entity(name = "Receptionist")
@Table(name = "receptionist")
@NamedQuery(
		name="Receptionist.findByUsernameAndPassword",
		query="SELECT r FROM Receptionist r WHERE r.username = :username AND r.password = :password")
public class Receptionist extends User {

	@ManyToOne
	@JoinColumn(name = "ID_hotel")
	private Hotel hotel;
	
	public Receptionist(String username, String password, String name, String surname, Hotel hotel) {
		super(username, password, name, surname);
		this.hotel = hotel;
	}

	public Receptionist() {
		super();
	}
}