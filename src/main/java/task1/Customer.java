package task1;

import java.util.*;
import javax.persistence.*;

@Entity(name = "Customer")
@Table(name = "customer")
public class Customer extends User {
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true )
	List<Reservation> reservations = new ArrayList<Reservation>();
}