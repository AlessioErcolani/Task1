package task1;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity(name = "Customer")
@Table(name = "customer")
@NamedQuery(
		name="Customer.findByUsernameAndPassword",
		query="SELECT c FROM Customer c WHERE c.username = :username AND c.password = :password")
@NamedQuery(
		name="Customer.findByUsername",
		query="SELECT c FROM Customer c WHERE c.username = :username")
@NamedQuery(
		name="Customer.findAll",
		query="SELECT c FROM Customer c ORDER BY c.surname")
public class Customer extends User {
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Reservation> reservations = new ArrayList<Reservation>();

	public Customer(String username, String password, String name, String surname) {
		super(username, password, name, surname);
	}
	
	public Customer(String username) {
		super(username);
	}

	public Customer() {
		super();
	}
	
	public void addReservation(Reservation reservation) {
		reservations.add(reservation);
		reservation.setCustomer(this);
	}
	
	public void removeReservation(Reservation reservation) {
		reservations.remove(reservation);
		reservation.setCustomer(null);
	}

	public List<Reservation> getReservations() {
		return reservations;
	}
	
	@Override
	public String toString() {
		return "Customer [ID=" + this.getId() + ", username=" + this.getUsername() + ", password=" + this.getPassword() + ", name=" + this.getName() + ", surname="
				+ this.getSurname() + "]";
	}
}