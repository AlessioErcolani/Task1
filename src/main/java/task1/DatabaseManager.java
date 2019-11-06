package task1;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import org.hibernate.exception.ConstraintViolationException;

import exc.*;

public class DatabaseManager {

	private EntityManagerFactory factory;
	private EntityManager entityManager;
	
	public DatabaseManager(String databaseSchema) {
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
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
	}
	
	/**
	 * @throws HotelAlreadtPresentException 
	 * Inserts a new Hotel in the database
	 * @param hotel the Hotel to add
	 * @throws DatabaseManagerException in case of errors
	 * @throws  
	 */
	public void addHotel(Hotel hotel) throws HotelAlreadyPresentException, DatabaseManagerException {
		try {
			setup();
			persistObject(hotel);
		} catch (PersistenceException ex) {
			throw new HotelAlreadyPresentException(ex.getMessage());
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
	public void addRoom(Room room) throws RoomAlreadyPresentException, DatabaseManagerException {
		try {
			setup();
			Hotel hotel = entityManager.find(Hotel.class, room.getHotel().getId());
			hotel.addRoom(room);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			try {
				commit();
			} catch (RollbackException ex) {
				throw new RoomAlreadyPresentException(ex.getMessage());
			}
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
	 * @throws ReservationAlreadyPresentException 
	 */
	public void addReservation(Reservation reservation) throws DatabaseManagerException, ReservationAlreadyPresentException {
		try {
			setup();
			Room room = entityManager.find(Room.class, reservation.getRoom().getId());
			room.addReservation(reservation);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			try {
				commit();
			} catch (RollbackException ex) {
				throw new ReservationAlreadyPresentException(ex.getMessage());
			}
			close();
		}
	}
	
	/**
	 * Update a reservation
	 * @param oldCheckInDate is the check-in date of the reservation to update
	 * @param oldRoom is the room of the reservation to update
 	 * @param newReservation is the new reservation
	 * @return the updated reservation
	 * @throws DatabaseManagerException in case of errors
	 */
	public void updateReservation(Reservation oldReservation, Reservation newReservation) throws DatabaseManagerException {
		try {
			setup();			
			Room oldRoom = entityManager.find(Room.class, oldReservation.getRoom().getId());
			oldRoom.removeReservation(oldReservation);
			entityManager.flush();
			Room newRoom = entityManager.find(Room.class, newReservation.getRoom().getId());
			newRoom.addReservation(newReservation);
		} catch (Exception ex) {		
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			try {
				commit();
			} catch (RollbackException ex) {
				throw new DatabaseManagerException(ex.getMessage());
			}
			close();
		}
	}
	
	/**
	 * Delete a reservation
	 * @param reservation the Reservation to delete
	 * @throws DatabaseManagerException
	 */
	public void deleteReservation(Reservation reservationToDelete) throws DatabaseManagerException {
		try {
			setup();			
			Reservation reservation = entityManager.find(Reservation.class, reservationToDelete.getId());
	        entityManager.remove(reservation);
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
	public List<Reservation> getUpcomingReservations(Customer customer) throws DatabaseManagerException {
		try {
			setup();
			List<Reservation> upcomingReservations = entityManager
					.createNamedQuery("Reservation.getByCustomer", Reservation.class)
					.setParameter("customerId", customer.getId())
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
	 * Get the list of upcoming reservations for the given hotel
	 * @param hotel
	 * @param date the minimum starting date for the check-in
	 * @return the list of reservations
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Reservation> getUpcomingReservations(Hotel hotel, Date date) throws DatabaseManagerException {
		try {
			setup();
			List<Reservation> upcomingReservations = entityManager
					.createNamedQuery("Reservation.getByHotel", Reservation.class)
					.setParameter("hotelId", hotel.getId())
					.setParameter("from", date, TemporalType.DATE)
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
	 * Get the list of rooms of an hotel that are reservable in a given period, i.e. they are available AND not occupied in the period
	 * @param hotel is the Hotel of the room
	 * @param startPeriod is the start date of the period
	 * @param endPeriod is the end date of the period
	 * @return a list of available rooms
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Room> getReservableRooms(Hotel hotel, Date startPeriod, Date endPeriod) throws DatabaseManagerException {
		try {
			setup();
			TypedQuery<Room> query = entityManager.createNamedQuery("Room.getReservableRoomsGivenPeriod", Room.class);
			query.setParameter("hotelId", hotel.getId());
			query.setParameter("startPeriod", startPeriod);
			query.setParameter("endPeriod", endPeriod);
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
	 * Get the list of rooms of an hotel that are unreservable in a given period, i.e. they are unavailable OR occupied in the given period
	 * @param hotel is the Hotel of the room
	 * @param startPeriod is the start date of the period
	 * @param endPeriod is the end date of the period
	 * @return a list of available rooms
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Room> getUnreservableRooms(Hotel hotel,Date startPeriod, Date endPeriod) throws DatabaseManagerException {
		try {
			setup();
			TypedQuery<Room> query = entityManager.createNamedQuery("Room.getUnreservableRoomsGivenPeriod", Room.class);
			query.setParameter("hotelId", hotel.getId());
			query.setParameter("startPeriod", startPeriod);
			query.setParameter("endPeriod", endPeriod);
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
	 * Set a room in an hotel as available
	 * @param hotel is the Hotel of the room
	 * @param roomNumber  is the number of the room
	 * @return the updated room
	 * @throws DatabaseManagerException in case of errors
	 */
	public Room setRoomAvailable(Room unavailableRoom) throws DatabaseManagerException {
		try {
			setup();
			Room room = entityManager.find(Room.class, unavailableRoom.getId());		
			room.setAvailable(true);
			return room;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
	}
	
	/**
	 * Set a room in an hotel as unavailable
	 * @param hotel is the Hotel of the room
	 * @param roomNumber is the number of the room
	 * @return the updated room
	 * @throws DatabaseManagerException in case of errors 
	 */
	public Room setRoomUnavailable(Room availableRoom) throws DatabaseManagerException {
		try {
			setup();
			Room room = entityManager.find(Room.class, availableRoom.getId());		
			room.setAvailable(false);
			return room;
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
	
	public List<Hotel> getAllHotels() throws DatabaseManagerException {
		try {
			setup();
			List<Hotel> hotels = entityManager
					.createNamedQuery("Hotel.findAll", Hotel.class)
					.getResultList();
			return hotels;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
	}
	
	public Hotel readHotel(String address) throws HotelNotFoundException, DatabaseManagerException {
		Hotel hotel = null;
		try {			
			setup();
			TypedQuery<Hotel> query = entityManager.createNamedQuery("Hotel.findByAddress", Hotel.class);
			query.setParameter("address", address);
			hotel = query.getSingleResult();	
		} catch (NoResultException nr) {
			throw new HotelNotFoundException();
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
		return hotel;
	}
	
	public Hotel getHotel(Long id) throws HotelNotFoundException, DatabaseManagerException {
		Hotel hotel = null;
		
		try {
			setup();
			hotel = entityManager.find(Hotel.class, id);
			if (hotel == null)
				throw new HotelNotFoundException();
			return hotel;
		} catch (HotelNotFoundException e) {
			throw new HotelNotFoundException(id.toString());
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commit();
			close();
		}
	}
	
	public Reservation getReservation(Long id) throws ReservationNotFoundException, DatabaseManagerException {
		Reservation reservation = null;
		
		try {
			setup();
			reservation = entityManager.find(Reservation.class, id);
			if (reservation == null)
				throw new ReservationNotFoundException(id.toString());
			return reservation;
		} catch (ReservationNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commit();
			close();
		}
	}
	
	public List<Room> getRoomsOfHotel(Hotel hotel) throws DatabaseManagerException {
		try {			
			setup();
			List<Room> rooms = entityManager
					.createNamedQuery("Room.findByHotel", Room.class)
					.setParameter("hotelId", hotel.getId())
					.getResultList();
			return rooms;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
	}
	
	public Room readRoom(long hotelId, int roomNumber) throws DatabaseManagerException, RoomNotFoundException {
		Room room = null;
		try {			
			setup();
			TypedQuery<Room> query = entityManager.createNamedQuery("Room.findByHotelAndNumber", Room.class);
			query.setParameter("hotelId", hotelId);
			query.setParameter("roomNumber", roomNumber);
			room = query.getSingleResult();	
		} catch (NoResultException nr) {
			throw new RoomNotFoundException();
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
		return room;
	}
	
	public Customer readCustomer(String username) throws DatabaseManagerException, CustomerNotFoundException {
		Customer customer = null;
		try {			
			setup();
			TypedQuery<Customer> query = entityManager.createNamedQuery("Customer.findByUsername", Customer.class);
			query.setParameter("username", username);
			customer = query.getSingleResult();	
		} catch (NoResultException nr) {
			throw new CustomerNotFoundException(username);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
		return customer;
	}
	
	public Receptionist readReceptionist(String username) throws DatabaseManagerException, ReceptionistNotFoundException {
		Receptionist receptionist = null;
		try {			
			setup();
			TypedQuery<Receptionist> query = entityManager.createNamedQuery("Receptionist.findByUsername", Receptionist.class);
			query.setParameter("username", username);
			receptionist = query.getSingleResult();	
		} catch (NoResultException nr) {
			throw new ReceptionistNotFoundException(username);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
		return receptionist;
	}
	
	public Reservation readReservation(long hotelId, int room, Date checkInDate) throws DatabaseManagerException, ReservationNotFoundException {
		Reservation reservation = null;
		try {			
			setup();
			TypedQuery<Reservation> query = entityManager.createNamedQuery("Reservation.getByHoteAndRoomAndCheckInDate", Reservation.class);
			query.setParameter("hotelId", hotelId);
			query.setParameter("roomNumber", room);
			query.setParameter("checkInDate", checkInDate);
			reservation = query.getSingleResult();	
		} catch (NoResultException nr) {
			throw new ReservationNotFoundException();
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
		return reservation;
	}

	/**
	 * Delete a customer 
	 * @param customer
	 * @throws DatabaseManagerException
	 */	
	public void deleteCustomer(Customer customer) throws DatabaseManagerException {
		try {
			setup();
			Customer ref = entityManager.find(Customer.class, customer.getId());
	        entityManager.remove(ref);
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
	 * @throws DatabaseManagerException
	 */
	public void deleteHotel(Hotel hotel) throws DatabaseManagerException {
		try {
			setup();
			Hotel ref = entityManager.find(Hotel.class, hotel.getId());
	        entityManager.remove(ref);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}	
	}
	
	public void deleteRoom(Room room) throws DatabaseManagerException {
		try {
			setup();
			Room ref = entityManager.find(Room.class, room.getId());
	        entityManager.remove(ref);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}	
	}
	
	/**
	 * Delete a receptionist
	 * @param receptionist
	 * @throws DatabaseManagerException
	 */
	public void deleteReceptionist(Receptionist receptionist) throws DatabaseManagerException {
		try {
			setup();
			Receptionist ref = entityManager.find(Receptionist.class, receptionist.getId());
	        entityManager.remove(ref);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}		
	}
	
	/**
	 * Update a room
	 * @param room
	 * @throws DatabaseManagerException
	 */
	public void updateRoom(Room room) throws DatabaseManagerException {
		try {
			setup();
			mergeObject(room);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commit();
			close();
		}
	}

	public static void populateDatabase(DatabaseManager manager) {
		try {
			manager.addCustomer(new Customer("federico", "pwd", "Federico", "Verdi"));
			manager.addCustomer(new Customer("alessio", "pwd", "Alessio", "Rossi"));
			manager.addCustomer(new Customer("chiara", "pwd", "Chiara", "Azzurri"));
			manager.addCustomer(new Customer("marco", "pwd", "Marco", "Bianchi"));
			manager.addCustomer(new Customer("luca", "pwd", "Luca", "Marroni"));
			manager.addCustomer(new Customer("sara", "pwd", "Sara", "Violi"));
			
			Hotel hotelRoma = new Hotel("Via Roma 26, Roma");
			manager.addHotel(hotelRoma);
			Hotel hotelMilano = new Hotel("Via Milano 27, Milano");
			manager.addHotel(hotelMilano);
			Hotel hotelBologna = new Hotel("Via Bologna 28, Bologna");
			manager.addHotel(hotelBologna);
			
			manager.addReceptionist(new Receptionist("r1", "pwd", "Laura", "Romani", hotelRoma));
			manager.addReceptionist(new Receptionist("r2", "pwd", "Francesco", "Bolognesi", hotelBologna));
			manager.addReceptionist(new Receptionist("r3", "pwd", "Mirco", "Rossi", hotelBologna));
			manager.addReceptionist(new Receptionist("r4", "pwd", "Luisa", "Milanelli", hotelMilano));
			manager.addReceptionist(new Receptionist("r5", "pwd", "Benedetta", "Vinci", hotelMilano));
			
			manager.addRoom(new Room(101, 4, hotelRoma));
			manager.addRoom(new Room(102, 3, hotelRoma));
			manager.addRoom(new Room(103, 2, hotelRoma));
			
			manager.addRoom(new Room(101, 2, hotelMilano));
			manager.addRoom(new Room(102, 3, hotelMilano));
			manager.addRoom(new Room(201, 4, hotelMilano));
			
			manager.addRoom(new Room(101, 4, hotelBologna));
			manager.addRoom(new Room(201, 3, hotelBologna));
			manager.addRoom(new Room(301, 2, hotelBologna));
			manager.addRoom(new Room(302, 2, hotelBologna, false));
			
			Room room401 = new Room(401, 5, hotelBologna);
			Customer customer401 = new Customer("piergiorgio", "pwd", "Piergiorgio", "Neri");
			manager.addRoom(room401);
			manager.addCustomer(customer401);
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(2019, 11 - 1, 15, 1, 0, 0);	
			Date checkIn = calendar.getTime();
			
			calendar.set(2019, 11 - 1, 19, 1, 0, 0);	
			Date checkOut = calendar.getTime();
			
			manager.addReservation(new Reservation(room401, checkIn, checkOut, customer401));
			
			calendar.set(2018, 11 - 1, 15, 1, 0, 0);	
			checkIn = calendar.getTime();
			
			calendar.set(2018, 11 - 1, 19, 1, 0, 0);	
			checkOut = calendar.getTime();
			
			manager.addReservation(new Reservation(room401, checkIn, checkOut, customer401));
			
		} catch (CustomerUsernameAlreadyPresentException ex) {
			System.out.println(ex.getMessage() + " already present (customer)");
		} catch (ReceptionistUsernameAlreadyPresentException ex) {
			System.out.println(ex.getMessage() + " already present (receptionist)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
