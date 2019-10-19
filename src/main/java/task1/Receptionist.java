package task1;

import javax.persistence.*;

@Entity(name = "Receptionist")
@Table(name = "receptionist")
public class Receptionist extends User {

	@ManyToOne
	@JoinColumn(name = "ID_hotel")
	private Hotel hotel;
}