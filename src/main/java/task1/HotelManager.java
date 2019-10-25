package task1;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.*;
import org.hibernate.exception.ConstraintViolationException;

import exc.*;

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

	private void close() {
		entityManager.close();
	}
	
	/**
	 * Inserts a Customer in the database
	 * @param customer the Customer to add
	 * @throws CustomerUsernameAlreadyPresentException if the username is already used
	 * @throws DatabaseManagerException 
	 */
	public void addCustomer(Customer customer) throws CustomerUsernameAlreadyPresentException, DatabaseManagerException {
		try {
			setup();
			persistObject(customer);
		} catch (PersistenceException pe) { // ConstraintViolationException
			Throwable t = pe.getCause();
			while ((t != null) && !(t instanceof ConstraintViolationException)) {
				t = t.getCause();
			}
			if (t instanceof ConstraintViolationException) {
				throw new CustomerUsernameAlreadyPresentException(customer.getUsername());
			}
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
	}
	
	/**
	 * Inserts a Receptionist in the database
	 * @param receptionist the Receptionist to add
	 * @throws ReceptionistUsernameAlreadyPresentException if the username is already used
	 * @throws DatabaseManagerException 
	 */
	public void addReceptionist(Receptionist receptionist) throws ReceptionistUsernameAlreadyPresentException, DatabaseManagerException {
		try {
			setup();
			persistObject(receptionist);
		} catch (PersistenceException pe) { // ConstraintViolationException
			Throwable t = pe.getCause();
			while ((t != null) && !(t instanceof ConstraintViolationException)) {
				t = t.getCause();
			}
			if (t instanceof ConstraintViolationException) {
				throw new ReceptionistUsernameAlreadyPresentException(receptionist.getUsername());
			}
		}catch (Exception ex) {
				System.out.println("SONO QUUII");
				throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
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
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
	}
	
	/**
	 * Inserts a room of the given hotel in the database.
	 * Note that this method does set the hotel field of the Room Object.
	 * @param hotel the Hotel of the room
	 * @param room the Room to add
	 * @throws DatabaseManagerException in case of errors
	 */
	public void addRoom(Hotel hotel, Room room) throws DatabaseManagerException {
		try {
			setup();
			room.setHotel(hotel);
			mergeObject(room);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
	}
		
	/**
	 * Inserts a reservation with the given parameters in the database.
	 * @param room the Room to book
	 * @param customer the Customer who ordered the Reservation
	 * @param checkIn check-in date
	 * @param checkOut check-out date
	 * @return the Reservation object corresponding to the inserted record
	 * @throws DatabaseManagerException in case of errors
	 */
	public Reservation addReservation(Room room, Customer customer, LocalDate checkIn, LocalDate checkOut) throws DatabaseManagerException {
		try {
			setup();
			Reservation reservation = new Reservation(room, checkIn, checkOut);
			reservation.setCustomer(customer);
			mergeObject(reservation);
			return reservation;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
	}
	
	/**
	 * Get the list of reservations for a customer
	 * @param customer
	 * @return the list of reservations
	 * @throws DatabaseManagerException
	 */
	public List<Reservation> getUpcomingReservation(Customer customer) throws DatabaseManagerException {
		try {
			setup();
			List<Reservation> upcomingReservations = entityManager
					.createNamedQuery("Reservation.getByCustomer", Reservation.class)
					.setParameter("customerId", customer.getID())
					.setParameter("checkInDate", LocalDate.now())
					.getResultList();
			return upcomingReservations;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
	}
	
	/**
	 * Get a list of rooms that are available in a given day in a given hotel
	 * @param hotel the Hotel of the room
	 * @param day the local date of availability
	 * @return a list of available rooms
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Room> getAvailableRooms(Hotel hotel, LocalDate day) throws DatabaseManagerException {
		try {
			setup();
			TypedQuery<Room> query = entityManager.createNamedQuery("Room.getAvailableRoomsGivenDay", Room.class);
			query.setParameter("hotelId", hotel.getHotelId());
			query.setParameter("day", day);
			List<Room> rooms = query.getResultList();
			return rooms;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
	}

	/**
	 * Checks for the authentication of a Customer through their username and password
	 * @param username customer's username
	 * @param password customer's password
	 * @return the authenticated Customer
	 * @throws CustomerAuthenticationFailure if authentication fails
	 */
	public Customer authenticateCustomer(String username, String password) throws CustomerAuthenticationFailure {
		Customer customer = null;
		try {
			setup();
			TypedQuery<Customer> query = entityManager.createNamedQuery("Customer.findByUsernameAndPassword", Customer.class);
			query.setParameter("username", username);
			query.setParameter("password", password);
			customer = query.getSingleResult();	
		} catch (Exception ex) {	
			 throw new CustomerAuthenticationFailure(username);
		} finally {
			commit();
			close();
		}
		return customer;
	}
	
	/**
	 * Checks for the authentication of a Receptionist through their username and password
	 * @param username receptionist's username
	 * @param password receptionist's password
	 * @return the authenticated Receptionist
	 * @throws ReceptionistAuthenticationFailure if authentication fails
	 */
	public Receptionist authenticateReceptionist(String username, String password) throws ReceptionistAuthenticationFailure {
		try {
			setup();
			TypedQuery<Receptionist> query = entityManager.createNamedQuery("Receptionist.findByUsernameAndPassword", Receptionist.class);
			query.setParameter("username", username);
			query.setParameter("password", password);
			Receptionist receptionist = query.getSingleResult();
			return receptionist;
		} catch (Exception ex) {			
			 throw new ReceptionistAuthenticationFailure(username);
		} finally {
			commit();
			close();
		}
	}

	
	public Hotel readHotel(String address) throws DatabaseManagerException {
		Hotel hotel = null;
		try {			
			setup();
			TypedQuery<Hotel> query = entityManager.createNamedQuery("Hotel.findByAddress", Hotel.class);
			query.setParameter("address", address);
			hotel = query.getSingleResult();	
		} catch (NoResultException nr) {
			return null;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
		return hotel;
	}
	
	/**
	 * Delete a customer 
	 * @param customer
	 * @throws CustomerNotFound
	 * @throws DatabaseManagerException
	 */	
	public void deleteCustomer(Customer customer) throws CustomerNotFound, DatabaseManagerException {
		try {
			setup();
			int rowAffected = entityManager
					.createNamedQuery("Customer.deleteCustomer")
					.setParameter("id", customer.getID())
					.executeUpdate();
			if (rowAffected == 0) {
				throw new CustomerNotFound();
			}
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}		
	}
	
	/**
	 * Delete an hotel 
	 * @param hotel
	 * @throws HotelNotFound
	 * @throws DatabaseManagerException
	 */
	
	public void deleteHotel(Hotel hotel) throws HotelNotFound, DatabaseManagerException {
		try {
			setup();
			int rowAffected = entityManager
					.createNamedQuery("Hotel.deleteHotel")
					.setParameter("hotelId", hotel.getHotelId())
					.executeUpdate();
			if (rowAffected == 0) {
				throw new HotelNotFound();
			}
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}		
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
			manager.addRoom(hotelBologna, new Room(302, 2, false));
			
			Room room401 = new Room(401, 5);
			Customer customer401 = new Customer("piergiorgio", "pwd", "Piergiorgio", "Neri");
			manager.addRoom(hotelBologna, room401);
			manager.addCustomer(customer401);
			LocalDate checkIn = LocalDate.parse("2019-11-15");
			LocalDate checkOut = LocalDate.parse("2019-11-19");
			manager.addReservation(room401, customer401, checkIn, checkOut);
			
			LocalDate occupiedDate = LocalDate.parse("2019-11-17");
			System.out.println("\nCheck for available rooms on " + occupiedDate);
			List<Room> rooms17Nov = manager.getAvailableRooms(hotelBologna, occupiedDate);
			if (rooms17Nov.isEmpty())
				System.out.println("No available rooms on " + occupiedDate);
			else
				for (Room r : rooms17Nov)
					System.out.println(r);
			
			LocalDate freeDate = LocalDate.parse("2019-11-21");
			System.out.println("\nCheck for available rooms on " + freeDate);
			List<Room> rooms21Nov = manager.getAvailableRooms(hotelBologna, freeDate);
			if (rooms21Nov.isEmpty())
				System.out.println("No available rooms on " + freeDate);
			else
				for (Room r : rooms21Nov)
					System.out.println(r);
			
		} catch (CustomerUsernameAlreadyPresentException ex) {
			System.out.println(ex.getMessage() + " already present (customer)");
		} catch (ReceptionistUsernameAlreadyPresentException ex) {
			System.out.println(ex.getMessage() + " already present (receptionist)");
		} catch (Exception e) {
			System.err.println("Something went wrong");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE); //OFF

		HotelManager manager = new HotelManager("hotel_chain");
		populateDatabase(manager);
		
		// TODO: move to JUnit test
		System.out.println("\nCheck successful login");
		try {
			// valid credentials
			Customer c = manager.authenticateCustomer("federico", "pwd");
			System.out.println("Hi, " + c);
		} catch (CustomerAuthenticationFailure e) {
			System.out.println("Successful authentication for " + e.getMessage());
		}
		
		// TODO: move to JUnit test
		System.out.println("\nCheck login with invalid password");
		try {
			// valid username, invalid password
			manager.authenticateCustomer("chiara", "wrong pwd");
		} catch (CustomerAuthenticationFailure e) {
			System.out.println("Authentication failed for " + e.getMessage());
		}
		
		// TODO: move to JUnit test
		System.out.println("\nCheck login with invalid username");
		try {
			// invalid username
			manager.authenticateCustomer("username that does not exists", "pwd");
		} catch (CustomerAuthenticationFailure e) {
			System.out.println("Authentication failed for " + e.getMessage());
		}
		
		// TODO: move to JUnit test
		System.out.println("\nCheck successful login");
		try {
			// valid credentials
			Receptionist r = manager.authenticateReceptionist("r1", "pwd");
			System.out.println("Hi, " + r);
		} catch (ReceptionistAuthenticationFailure e) {
			System.out.println("Successful authentication for " + e.getMessage());
		}
		
		// TODO: move to JUnit test
		System.out.println("\nCheck login with invalid password");
		try {
			// valid username, invalid password
			manager.authenticateReceptionist("r2", "wrong pwd");
		} catch (ReceptionistAuthenticationFailure e) {
			System.out.println("Authentication failed for " + e.getMessage());
		}
		
		// TODO: move to JUnit test
		System.out.println("\nCheck login with invalid username");
		try {
			// invalid username
			manager.authenticateReceptionist("username that does not exists", "pwd");
		} catch (ReceptionistAuthenticationFailure e) {
			System.out.println("Authentication failed for " + e.getMessage());
		}
		
		// TODO: move to JUnit test
		System.out.println("\nGet upcoming reservation for an user");
		try {
			Customer customer401 = manager.authenticateCustomer("piergiorgio", "pwd");
			List<Reservation> upcomingReservations = manager.getUpcomingReservation(customer401);
			for(Reservation reservation : upcomingReservations)
				System.out.println(reservation.toString());
		} catch (Exception ex) {
			System.out.println("An error occur in getting all the reservations");
		}
				
		manager.exit();
	}
}
