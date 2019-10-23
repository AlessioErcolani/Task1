package task1;

import java.util.*;
import javax.persistence.*;

@Entity(name = "Customer")
@Table(name = "customer")
@NamedQuery(
		name="Customer.findByUsernameAndPassword",
		query="SELECT c FROM Customer c WHERE c.username = :username AND c.password = :password")
public class Customer extends User {

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Reservation> reservations = new ArrayList<Reservation>();

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((reservations == null) ? 0 : reservations.hashCode());
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
		Customer other = (Customer) obj;
		if (reservations == null) {
			if (other.reservations != null)
				return false;
		} else if (!reservations.equals(other.reservations))
			return false;
		return true;
	}
}