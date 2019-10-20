package task1;

import java.util.*;
import javax.persistence.*;

@Entity(name = "Customer")
@Table(name = "customer")
public class Customer extends User {
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true )
	List<Reservation> reservations = new ArrayList<Reservation>();
	
	public Customer(String username, String password, String name, String surname) {
		super(username, password, name, surname);
	}
	
	public Customer() {
		super();
	}
	
	public void addReservation(Reservation reservation) {
		reservations.add(reservation);
		reservation.setCustomer(this);
	}
}