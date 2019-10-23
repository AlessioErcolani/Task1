package task1;

import java.util.logging.Level;

import javax.persistence.*;
import org.hibernate.exception.ConstraintViolationException;

public class HotelManager {

	private EntityManagerFactory factory;
	private EntityManager entityManager;
	
	public HotelManager(String databaseSchema) {
		factory = Persistence.createEntityManagerFactory(databaseSchema);
	}
	
	public void exit() {
		factory.close();
	}
	
	private void setup() {
		entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
	}
	
	private void commit() {
		entityManager.getTransaction().commit();
	}
	
	private void persistObject(Object obj) {		
		entityManager.persist(obj);		
	}
	
	private void mergeObject(Object obj) {
		entityManager.merge(obj);
	}

	/**
	 * Inserts a Customer in the database
	 * @param customer the Customer to add
	 * @throws CustomerUsernameAlreadyPresentException if the username is already used
	 */
	public void addCustomer(Customer customer) throws CustomerUsernameAlreadyPresentException {
		try {
			setup();
			persistObject(customer);
			commit();
		} catch (PersistenceException pe) { // ConstraintViolationException
			Throwable t = pe.getCause();
			while ((t != null) && !(t instanceof ConstraintViolationException)) {
				t = t.getCause();
			}
			if (t instanceof ConstraintViolationException) {
				throw new CustomerUsernameAlreadyPresentException(customer.getUsername());
			}
		} finally {
			entityManager.close();
		}
	}
	
	/**
	 * Inserts a Receptionist in the database
	 * @param receptionist the Receptionist to add
	 * @throws ReceptionistUsernameAlreadyPresentException if the username is already used
	 */
	public void addReceptionist(Receptionist receptionist) throws ReceptionistUsernameAlreadyPresentException {
		try {
			setup();
			persistObject(receptionist);
			commit();
		} catch (PersistenceException pe) { // ConstraintViolationException
			Throwable t = pe.getCause();
			while ((t != null) && !(t instanceof ConstraintViolationException)) {
				t = t.getCause();
			}
			if (t instanceof ConstraintViolationException) {
				throw new ReceptionistUsernameAlreadyPresentException(receptionist.getUsername());
			}
		} finally {
			entityManager.close();
		}
	}
	
	/**
	 * Inserts a new Hotel in the database
	 * @param hotel the Hotel to add
	 * @throws DatabaseManagerException in case of errors
	 */
	public void addHotel(Hotel hotel) throws DatabaseManagerException {
		try {
			setup();
			persistObject(hotel);
			commit();
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
	}
	
	/**
	 * Inserts a room of the given hotel in the database
	 * @param hotel the Hotel of the room
	 * @param room the Room to add
	 * @throws DatabaseManagerException in case of errors
	 */
	public void addRoom(Hotel hotel, Room room) throws DatabaseManagerException {
		try {
			setup();
			room.setHotel(hotel);
			mergeObject(room);
			commit();
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
	}
	
	
	
	
	public Customer authenticateCustomer(String username, String password) throws DatabaseManagerException {
		Customer customer = null;
		try {
			setup();
			TypedQuery<Customer> query = entityManager.createNamedQuery("Customer.findByUsernameAndPassword", Customer.class);
			query.setParameter("username", username);
			query.setParameter("password", password);
			customer = query.getSingleResult();
			commit();
		} catch (NoResultException nr) {
			return null;
		} catch (Exception ex) {			
			 throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
		return customer;
	}

	public Hotel readHotel(String address) throws DatabaseManagerException {
		Hotel hotel = null;
		try {			
			setup();
			TypedQuery<Hotel> query = entityManager.createNamedQuery("Hotel.findByAddress", Hotel.class);
			query.setParameter("address", address);
			hotel = query.getSingleResult();
			commit();	
		} catch (NoResultException nr) {
			return null;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
		return hotel;
	}

	
	public static void populateDatabase(HotelManager manager) {
		try {
			manager.addCustomer(new Customer("federico", "pwd", "Federico", "Verdi"));
			manager.addCustomer(new Customer("alessio", "pwd", "Alessio", "Rossi"));
			manager.addCustomer(new Customer("chiara", "pwd", "Chiara", "Azzurri"));
			manager.addCustomer(new Customer("marco", "pwd", "Marco", "Bianchi"));
			manager.addCustomer(new Customer("luca", "pwd", "Luca", "Arancioni"));
			manager.addCustomer(new Customer("sara", "pwd", "Sara", "Violi"));
			
			Hotel hotelRoma = new Hotel("Via Roma 26, Roma");
			manager.addHotel(hotelRoma);
			Hotel hotelMilano = new Hotel("Via Milano 27, Milano");
			manager.addHotel(hotelMilano);
			Hotel hotelBologna = new Hotel("Via Bologna 28, Bologna");
			manager.addHotel(hotelBologna);
			
			manager.addReceptionist(new Receptionist("r1", "pwd", "Laura", "Romani", hotelRoma));
			manager.addReceptionist(new Receptionist("r2", "pwd", "Francesco", "Bolognesi", hotelBologna));
			
			manager.addRoom(hotelRoma, new Room(101, 4));
			manager.addRoom(hotelRoma, new Room(101, 4));
			manager.addRoom(hotelRoma, new Room(102, 3));
			manager.addRoom(hotelRoma, new Room(103, 2));
			
			manager.addRoom(hotelMilano, new Room(101, 2));
			manager.addRoom(hotelMilano, new Room(102, 3));
			manager.addRoom(hotelMilano, new Room(201, 4));
			
			manager.addRoom(hotelBologna, new Room(101, 4));
			manager.addRoom(hotelBologna, new Room(201, 3));
			manager.addRoom(hotelBologna, new Room(301, 2));
			manager.addRoom(hotelBologna, new Room(302, 2));
			manager.addRoom(hotelBologna, new Room(303, 4));
			
		} catch (CustomerUsernameAlreadyPresentException ex) {
			System.out.println(ex.getMessage() + " already present (customer");
		} catch (ReceptionistUsernameAlreadyPresentException ex) {
			System.out.println(ex.getMessage() + " already present (receptionist");
		} catch (Exception e) {
			System.err.println("Something went wrong");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);

		HotelManager manager = new HotelManager("hotel_chain");
		populateDatabase(manager);
		manager.exit();
	}
}
