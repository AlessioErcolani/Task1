package task1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;
import org.apache.commons.cli.ParseException;
import org.hibernate.exception.ConstraintViolationException;

import exc.*;

public class DatabaseManager {

	private EntityManagerFactory factory;
	private EntityManager entityManager;
	public final KeyValueDatabaseManager keyValue;

	public DatabaseManager(String databaseSchema) throws DatabaseManagerException {

		// relational database
		factory = Persistence.createEntityManagerFactory(databaseSchema);

		// key-value database
		try {
			keyValue = new KeyValueDatabaseManager(databaseSchema);
		} catch (KeyValueDatabaseManagerException kvd) {
			throw new DatabaseManagerException();
		}
	}

	public void exit() {
		factory.close();
	}

	private void beginTransaction() {
		entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
	}

	private void commitTransaction() {
		entityManager.getTransaction().commit();
	}

	private void persistObject(Object obj) {
		entityManager.persist(obj);
	}

	private void close() {
		entityManager.close();
	}

	/* OK */
	/**
	 * Insert a Customer in the database
	 * 
	 * @param customer the Customer to add
	 * @throws CustomerUsernameAlreadyPresentException if the username is already
	 *                                                 used
	 * @throws DatabaseManagerException                in case of other errors
	 */
	public void insertCustomer(Customer customer)
			throws CustomerUsernameAlreadyPresentException, DatabaseManagerException {
		try {
			beginTransaction();
			persistObject(customer);
		} catch (PersistenceException pe) { // ConstraintViolationException
			Throwable t = pe.getCause();
			while ((t != null) && !(t instanceof ConstraintViolationException)) {
				t = t.getCause();
			}
			if (t instanceof ConstraintViolationException) {
				throw new CustomerUsernameAlreadyPresentException(customer.getUsername());
			}
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Insert a Receptionist in the database
	 * 
	 * @param receptionist the Receptionist to add
	 * @throws ReceptionistUsernameAlreadyPresentException if the username is
	 *                                                     already used
	 * @throws DatabaseManagerException                    in case of other errors
	 */
	public void insertReceptionist(Receptionist receptionist)
			throws ReceptionistUsernameAlreadyPresentException, DatabaseManagerException {
		try {
			beginTransaction();
			persistObject(receptionist);
		} catch (PersistenceException pe) { // ConstraintViolationException
			Throwable t = pe.getCause();
			while ((t != null) && !(t instanceof ConstraintViolationException)) {
				t = t.getCause();
			}
			if (t instanceof ConstraintViolationException) {
				throw new ReceptionistUsernameAlreadyPresentException(receptionist.getUsername());
			}
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Insert a Hotel in the database
	 * 
	 * @param hotel the Hotel to add
	 * @throws HotelAlreadyPresentException if the address of the hotel is already
	 *                                      used
	 * @throws DatabaseManagerException     in case of other errors
	 */
	public void insertHotel(Hotel hotel) throws HotelAlreadyPresentException, DatabaseManagerException {
		try {
			beginTransaction();
			persistObject(hotel);
		} catch (PersistenceException e) {
			throw new HotelAlreadyPresentException(e.getMessage());
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Insert a Room in the database
	 * 
	 * @param room the Room to add
	 * @throws RoomAlreadyPresentException if the room is already present
	 * @throws DatabaseManagerException    in case of other errors
	 */
	public void insertRoom(Room room) throws RoomAlreadyPresentException, DatabaseManagerException {
		try {
			beginTransaction();
			persistObject(room);
		} catch (PersistenceException e) {
			throw new RoomAlreadyPresentException(e.getMessage());
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Insert a Reservation in the database
	 * 
	 * @param hotelId     the id of the hotel
	 * @param roomNumber  the number of the room
	 * @param username    the username of the customer
	 * @param reservation a Reservation object that contains the check-in and
	 *                    check-out dates
	 * @throws RoomNotFoundException              if the room does not exist
	 * @throws RoomAlreadyBookedException         if the room is already booked in
	 *                                            the period
	 * @throws CustomerNotFoundException          if the customer does not exist
	 * @throws ReservationAlreadyPresentException if the reservation already exist
	 * @throws DatabaseManagerException           in case if other errors
	 */
	public Reservation insertReservation(long hotelId, int roomNumber, String username, Reservation reservation)
			throws RoomNotFoundException, RoomAlreadyBookedException, CustomerNotFoundException,
			DatabaseManagerException, ReservationAlreadyPresentException {
		try {
			beginTransaction();

			// retrieve the room
			Room room;
			try {
				room = entityManager.createNamedQuery("Room.findByHotelAndNumber", Room.class)
						.setParameter("hotelId", hotelId).setParameter("roomNumber", roomNumber).getSingleResult();
			} catch (NoResultException nr) {
				throw new RoomNotFoundException();
			}

			// check if room is reservable
			List<Room> reservableRooms = entityManager
					.createNamedQuery("Room.getReservableRoomsGivenPeriod", Room.class).setParameter("hotelId", hotelId)
					.setParameter("startPeriod", reservation.getCheckInDate(), TemporalType.DATE)
					.setParameter("endPeriod", reservation.getCheckOutDate(), TemporalType.DATE).getResultList();
			if (!reservableRooms.contains(room))
				throw new RoomAlreadyBookedException();

			// retrieve the customer
			Customer customer;
			try {
				customer = entityManager.createNamedQuery("Customer.findByUsername", Customer.class)
						.setParameter("username", username).getSingleResult();
			} catch (NoResultException nr) {
				throw new CustomerNotFoundException(username);
			}

			// add reservation
			reservation.setRoom(room);
			reservation.setCustomer(customer);
			persistObject(reservation);

			// simulation key-value down
			if (keyValue.isAvailable) {
				updateKeyValue(null, reservation);
			} else {
				writeErrorLog("[INSERT]: " + new Booking(reservation.getCustomer().getName(),
						reservation.getCustomer().getSurname(), Integer.toString(reservation.getRoom().getNumber()))
						+ "\n");
			}

			return reservation;
		} catch (RoomNotFoundException | RoomAlreadyBookedException | CustomerNotFoundException e) {
			throw e;
		} catch (PersistenceException e) {
			throw new ReservationAlreadyPresentException();
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Update the key-value database
	 * 
	 * @param oldReservation if not null delete the corresponding records in the
	 *                       key-value database
	 * @param newReservation if not null insert the corresponding records in the
	 *                       key-value database
	 */
	public void updateKeyValue(Reservation oldReservation, Reservation newReservation) {
		if (oldReservation != null) {
			try {
				keyValue.deleteBooking(Long.toString(oldReservation.getId()));
			} catch (KeyValueDatabaseManagerException e) {
				String error = "Error in deleting reservation with id " + oldReservation.getId() + "\n";
				writeErrorLog("[ERR_DELETE]: " + error + "\n");
			}
		}

		if (newReservation != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Booking booking = new Booking(newReservation.getCustomer().getName(),
							newReservation.getCustomer().getSurname(),
							Integer.toString(newReservation.getRoom().getNumber()));
					try {
						keyValue.insertBooking(Long.toString(newReservation.getId()), booking);
					} catch (KeyValueDatabaseManagerException | BookingAlreadyPresentException e) {
						String error = "Error in writing reservation for " + booking.getName() + " "
								+ booking.getSurname() + " in room " + booking.getRoomNumber() + "\n";
						writeErrorLog("[ERR_UPDATE]: " + error + "\n");
					}
				}
			}).start();
		}
	}

	/* OK */
	/**
	 * Write in the "errorLog.txt" file every time that it is not possible update
	 * the key-value database
	 * 
	 * @param error the string to write in the file
	 */
	public void writeErrorLog(String error) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("errorLog.txt"), true));
			writer.append(error);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* OK */
	/**
	 * Update an existing reservation
	 * 
	 * @param oldHotelId     the id of the hotel in the reservation to update
	 * @param oldRoomNumber  the room number in the reservation to update
	 * @param oldCheckInDate the check-in date in the reservation to update
	 * @param newHotelId     the id of the new hotel
	 * @param newRoomNumber  the new room number
	 * @param newUsername    the new customer's username
	 * @param newCheckIn     the new check-in date
	 * @param newCheckOut    the new check-out date
	 * @throws ReservationNotFoundException       when the reservation to update
	 *                                            does not exist
	 * @throws ParseException                     when check-in date is greater than
	 *                                            check-out date
	 * @throws RoomNotFoundException              when the room of the new
	 *                                            reservation does not exist
	 * @throws CustomerNotFoundException          when the customer of the new
	 *                                            reservation does not exist
	 * @throws ReservationAlreadyPresentException when old and new reservations are
	 *                                            equal
	 * @throws RoomAlreadyBookedException         when the room of the new
	 *                                            reservation is already booked
	 * @throws DatabaseManagerException           in case of other errors
	 */
	public Reservation updateReservation(long oldHotelId, int oldRoomNumber, Date oldCheckInDate, long newHotelId,
			int newRoomNumber, String newUsername, Date newCheckIn, Date newCheckOut)
			throws ParseException, ReservationNotFoundException, RoomNotFoundException, CustomerNotFoundException,
			ReservationAlreadyPresentException, RoomAlreadyBookedException, DatabaseManagerException {
		Reservation tmp = null, reservation = null;
		try {
			beginTransaction();
			boolean updateRoom = false, updateCustomer = false;

			// retrieve the reservation to update
			try {
				reservation = entityManager
						.createNamedQuery("Reservation.getByHotelAndRoomAndCheckInDate", Reservation.class)
						.setParameter("hotelId", oldHotelId).setParameter("roomNumber", oldRoomNumber)
						.setParameter("checkInDate", oldCheckInDate, TemporalType.DATE).getSingleResult();
			} catch (NoResultException nr) {
				throw new ReservationNotFoundException();
			}
			tmp = reservation;

			if (newCheckOut == null)
				newCheckOut = reservation.getCheckOutDate();

			if (newCheckOut.before(newCheckIn) || newCheckOut.equals(newCheckIn))
				throw new ParseException("Check-out date must be greater than check-in date");

			if (newUsername == null)
				newUsername = reservation.getCustomer().getUsername();

			Room newRoom = null;
			// if the new hotel is different or the new room is different or new dates are
			// different
			// retrieve the new room and check if it is available in the period
			if ((newHotelId != oldHotelId) || (newRoomNumber != reservation.getRoom().getNumber())
					|| (!newCheckIn.equals(reservation.getCheckInDate())
							|| (!newCheckOut.equals(reservation.getCheckOutDate())))) {
				try {
					newRoom = entityManager.createNamedQuery("Room.findByHotelAndNumber", Room.class)
							.setParameter("hotelId", newHotelId).setParameter("roomNumber", newRoomNumber)
							.getSingleResult();
				} catch (NoResultException nr) {
					throw new RoomNotFoundException();
				}

				List<Room> reservableRooms = entityManager
						.createNamedQuery("Room.getReservableRoomsGivenPeriod", Room.class)
						.setParameter("hotelId", newHotelId).setParameter("startPeriod", newCheckIn, TemporalType.DATE)
						.setParameter("endPeriod", newCheckOut, TemporalType.DATE).getResultList();

				if (!newRoom.equals(reservation.getRoom()) && !reservableRooms.contains(newRoom))
					throw new RoomAlreadyBookedException();

				updateRoom = true;
			}

			Customer newCustomer = null;
			// if the new customer is different
			// retrieve the new customer
			if (newUsername != reservation.getCustomer().getUsername()) {
				try {
					newCustomer = entityManager.createNamedQuery("Customer.findByUsername", Customer.class)
							.setParameter("username", newUsername).getSingleResult();
				} catch (NoResultException nr) {
					throw new CustomerNotFoundException(newUsername);
				}

				updateCustomer = true;
			}

			if (!updateCustomer && !updateRoom)
				throw new ReservationAlreadyPresentException();

			if (updateRoom) {
				reservation.setRoom(newRoom);
				reservation.setCheckInDate(newCheckIn);
				reservation.setCheckOutDate(newCheckOut);
			}
			if (updateCustomer) {
				reservation.setCustomer(newCustomer);
			}

			// used to simulate the key-value down
			if (keyValue.isAvailable) {
				updateKeyValue(tmp, reservation);
			} else {
				writeErrorLog("[UPDATE]: " + new Booking(reservation.getCustomer().getName(),
						reservation.getCustomer().getSurname(), Integer.toString(reservation.getRoom().getNumber()))
						+ "\n");
			}

			return reservation;
		} catch (ParseException | ReservationNotFoundException | RoomNotFoundException | CustomerNotFoundException
				| ReservationAlreadyPresentException | RoomAlreadyBookedException e) {
			throw e;
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Delete a reservation
	 * 
	 * @param hotelId    the id of the hotel of the reservation
	 * @param roomNumber the room number of the reservation
	 * @param checkIn    the check-in date of the reservation
	 * @return the deleted reservation
	 * @throws ReservationNotFoundException if the reservation does not exist
	 * @throws DatabaseManagerException     in case of other errors
	 */
	public Reservation deleteReservation(long hotelId, int roomNumber, Date checkIn)
			throws DatabaseManagerException, ReservationNotFoundException {
		try {
			Reservation reservation;
			beginTransaction();
			try {
				reservation = entityManager
						.createNamedQuery("Reservation.getByHotelAndRoomAndCheckInDate", Reservation.class)
						.setParameter("hotelId", hotelId).setParameter("roomNumber", roomNumber)
						.setParameter("checkInDate", checkIn, TemporalType.DATE).getSingleResult();
			} catch (NoResultException nr) {
				throw new ReservationNotFoundException();
			}

			entityManager.remove(reservation);

			// delete key-value pair
			if (!keyValue.isAvailable) {
				updateKeyValue(reservation, null);
			}

			return reservation;
		} catch (ReservationNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Get the list of reservations for a customer
	 * 
	 * @param customer
	 * @return the list of customer's upcoming reservations
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Reservation> retrieveUpcomingCustomerReservations(Customer customer) throws DatabaseManagerException {
		try {
			beginTransaction();
			List<Reservation> upcomingReservations = entityManager
					.createNamedQuery("Reservation.getByCustomer", Reservation.class)
					.setParameter("customerId", customer.getId()).getResultList();
			return upcomingReservations;
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Retrieve reservations in a hotel starting from a given date
	 * 
	 * @param hotelId the id of the hotel
	 * @param date    the begin date of the period
	 * @return the list of upcoming reservations
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Reservation> retrieveUpcomingReservations(long hotelId, Date date) throws DatabaseManagerException {
		try {
			beginTransaction();
			List<Reservation> upcomingReservations = entityManager
					.createNamedQuery("Reservation.getByHotel", Reservation.class).setParameter("hotelId", hotelId)
					.setParameter("from", date, TemporalType.DATE).getResultList();
			return upcomingReservations;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Retrieve the list of bookable rooms in an hotel in a given period of time
	 * 
	 * @param hotelId     the id of the hotel
	 * @param startPeriod the begin day of the period
	 * @param endPeriod   the end day of the period
	 * @return the list of bookable rooms
	 * @throws HotelNotFoundException   when the hotel does not exist
	 * @throws DatabaseManagerException in case of other errors
	 */
	public List<Room> retrieveReservableRooms(Long hotelId, Date startPeriod, Date endPeriod)
			throws DatabaseManagerException, HotelNotFoundException {
		try {
			beginTransaction();

			Hotel hotel = entityManager.find(Hotel.class, hotelId);
			if (hotel == null)
				throw new HotelNotFoundException(hotelId.toString());

			List<Room> rooms = entityManager.createNamedQuery("Room.getReservableRoomsGivenPeriod", Room.class)
					.setParameter("hotelId", hotelId).setParameter("startPeriod", startPeriod, TemporalType.DATE)
					.setParameter("endPeriod", endPeriod, TemporalType.DATE).getResultList();
			return rooms;
		} catch (HotelNotFoundException e) {
			throw e;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Retrieve the list of non-bookable rooms in a hotel in a given period
	 * 
	 * @param hotelId     the id of the hotel
	 * @param startPeriod the begin day of the period
	 * @param endPeriod   the end day of the period
	 * @return the list of non-bookable rooms
	 * @throws HotelNotFoundException   if the hotel does not exist
	 * @throws DatabaseManagerException in case of other errors
	 */
	public List<Room> retrieveUnreservableRooms(Long hotelId, Date startPeriod, Date endPeriod)
			throws DatabaseManagerException, HotelNotFoundException {
		try {
			beginTransaction();

			Hotel hotel = entityManager.find(Hotel.class, hotelId);
			if (hotel == null)
				throw new HotelNotFoundException(hotelId.toString());

			List<Room> rooms = entityManager.createNamedQuery("Room.getUnreservableRoomsGivenPeriod", Room.class)
					.setParameter("hotelId", hotel.getId()).setParameter("startPeriod", startPeriod, TemporalType.DATE)
					.setParameter("endPeriod", endPeriod, TemporalType.DATE).getResultList();
			return rooms;
		} catch (HotelNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Set a room available or unavailable
	 * 
	 * @param hotelId      the id of the hotel of the room
	 * @param roomNumber   the room number
	 * @param availability is true to set the room available and false to set in
	 *                     unavailable
	 * @return the updated room
	 * @throws RoomNotFoundException    if the room does not exist
	 * @throws DatabaseManagerException in case of other errors
	 */
	public Room updateRoomAvailability(long hotelId, int roomNumber, boolean availability)
			throws RoomNotFoundException, DatabaseManagerException {
		Room room = null;
		try {
			beginTransaction();
			room = entityManager.createNamedQuery("Room.findByHotelAndNumber", Room.class)
					.setParameter("hotelId", hotelId).setParameter("roomNumber", roomNumber).getSingleResult();
			room.setAvailable(availability);
			return room;
		} catch (NoResultException nr) {
			throw new RoomNotFoundException();
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Change the password of the customer
	 * 
	 * @param customer    the customer who wants change the password
	 * @param newPassword the new password
	 * @return the customer
	 * @throws DatabaseManagerException in case of errors
	 */
	public Customer updatePassword(Customer customer, String newPassword) throws DatabaseManagerException {
		try {
			beginTransaction();
			Customer ref = entityManager.find(Customer.class, customer.getId());
			ref.setPassword(newPassword);
			return ref;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Checks for the authentication of a Customer through their username and
	 * password
	 * 
	 * @param username customer's username
	 * @param password customer's password
	 * @return the authenticated Customer
	 * @throws CustomerAuthenticationFailure if authentication fails
	 */
	public Customer authenticateCustomer(String username, String password) throws CustomerAuthenticationFailure {
		try {
			beginTransaction();
			Customer customer = entityManager.createNamedQuery("Customer.findByUsernameAndPassword", Customer.class)
					.setParameter("username", username).setParameter("password", password).getSingleResult();
			return customer;
		} catch (Exception ex) {
			throw new CustomerAuthenticationFailure(username);
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Checks for the authentication of a Receptionist through their username and
	 * password
	 * 
	 * @param username receptionist's username
	 * @param password receptionist's password
	 * @return the authenticated Receptionist
	 * @throws ReceptionistAuthenticationFailure if authentication fails
	 */
	public Receptionist authenticateReceptionist(String username, String password)
			throws ReceptionistAuthenticationFailure {
		try {
			beginTransaction();
			Receptionist receptionist = entityManager
					.createNamedQuery("Receptionist.findByUsernameAndPassword", Receptionist.class)
					.setParameter("username", username).setParameter("password", password).getSingleResult();
			return receptionist;
		} catch (Exception ex) {
			throw new ReceptionistAuthenticationFailure(username);
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Retrieve the list of hotels
	 * 
	 * @return the list of hotels
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Hotel> retrieveHotels() throws DatabaseManagerException {
		try {
			beginTransaction();
			List<Hotel> hotels = entityManager.createNamedQuery("Hotel.findAll", Hotel.class).getResultList();
			return hotels;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Return an hotel given its address
	 * 
	 * @param address of the hotel
	 * @return the hotel
	 * @throws HotelNotFoundException   if the hotel does not exist
	 * @throws DatabaseManagerException in case of errors
	 */
	public Hotel retrieveHotel(String address) throws HotelNotFoundException, DatabaseManagerException {
		Hotel hotel = null;
		try {
			beginTransaction();
			hotel = entityManager.createNamedQuery("Hotel.findByAddress", Hotel.class).setParameter("address", address)
					.getSingleResult();
			return hotel;
		} catch (NoResultException nr) {
			throw new HotelNotFoundException();
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Return an hotel given its id
	 * 
	 * @param id the unique id of the hotel
	 * @return the hotel
	 * @throws HotelNotFoundException   if the hotel does not exists
	 * @throws DatabaseManagerException in case of errors
	 */
	public Hotel retrieveHotel(Long id) throws HotelNotFoundException, DatabaseManagerException {
		Hotel hotel = null;

		try {
			beginTransaction();
			hotel = entityManager.find(Hotel.class, id);
			if (hotel == null)
				throw new HotelNotFoundException(id.toString());
			return hotel;
		} catch (HotelNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Retrieve the list of all the rooms in a hotel
	 * 
	 * @param hotelId the id of the hotel
	 * @return the list of rooms
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Room> retrieveHotelRooms(long hotelId) throws DatabaseManagerException {
		try {
			beginTransaction();
			List<Room> rooms = entityManager.createNamedQuery("Room.findByHotel", Room.class)
					.setParameter("hotelId", hotelId).getResultList();
			return rooms;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Return a room given an hotelId and the room number
	 * 
	 * @param hotelId    the unique id of the hotel
	 * @param roomNumber
	 * @return the room
	 * @throws DatabaseManagerException in case of errors
	 * @throws RoomNotFoundException    if the room does not exist
	 */
	public Room retrieveRoom(long hotelId, int roomNumber) throws DatabaseManagerException, RoomNotFoundException {

		try {
			beginTransaction();
			Room room = entityManager.createNamedQuery("Room.findByHotelAndNumber", Room.class)
					.setParameter("hotelId", hotelId).setParameter("roomNumber", roomNumber).getSingleResult();
			return room;
		} catch (NoResultException nr) {
			throw new RoomNotFoundException();
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Return a customer given the username
	 * 
	 * @param username
	 * @return the customer
	 * @throws CustomerNotFoundException if the customer with that username does not
	 *                                   exist
	 * @throws DatabaseManagerException  in case of errors
	 */
	public Customer retrieveCustomer(String username) throws DatabaseManagerException, CustomerNotFoundException {
		Customer customer = null;
		try {
			beginTransaction();
			customer = entityManager.createNamedQuery("Customer.findByUsername", Customer.class)
					.setParameter("username", username).getSingleResult();
			return customer;
		} catch (NoResultException nr) {
			throw new CustomerNotFoundException(username);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Return a receptionist given the username
	 * 
	 * @param username
	 * @return the receptionist
	 * @throws DatabaseManagerException      in case of errors
	 * @throws ReceptionistNotFoundException if the receptionist with that username
	 *                                       does not exist
	 */
	public Receptionist retrieveReceptionist(String username)
			throws DatabaseManagerException, ReceptionistNotFoundException {
		Receptionist receptionist = null;
		try {
			beginTransaction();
			receptionist = entityManager.createNamedQuery("Receptionist.findByUsername", Receptionist.class)
					.setParameter("username", username).getSingleResult();
			return receptionist;
		} catch (NoResultException nr) {
			throw new ReceptionistNotFoundException(username);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Delete a Customer
	 * 
	 * @param customer the Customer to delete
	 * @throws CustomerNotFoundException if customer does not exist
	 * @throws DatabaseManagerException  in case of other errors
	 */
	public void deleteCustomer(Customer customer) throws CustomerNotFoundException, DatabaseManagerException {
		try {
			beginTransaction();
			Customer ref = entityManager.find(Customer.class, customer.getId());
			if (ref == null)
				throw new CustomerNotFoundException(customer.getUsername());
			entityManager.remove(ref);
		} catch (CustomerNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Delete a Hotel
	 * 
	 * @param hotel the Hotel to delete
	 * @throws HotelNotFoundException   if the hotel does not exist
	 * @throws DatabaseManagerException in case of other errors
	 */
	public void deleteHotel(Hotel hotel) throws HotelNotFoundException, DatabaseManagerException {
		try {
			beginTransaction();
			Hotel ref = entityManager.find(Hotel.class, hotel.getId());
			if (ref == null)
				throw new HotelNotFoundException();
			entityManager.remove(ref);
		} catch (HotelNotFoundException e) {
			throw e;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Delete a Room
	 * 
	 * @param room the Room to delete
	 * @throws RoomNotFoundException    if the room does not exist
	 * @throws DatabaseManagerException in case of other errors
	 */
	public void deleteRoom(Room room) throws RoomNotFoundException, DatabaseManagerException {
		try {
			beginTransaction();
			Room ref = entityManager.find(Room.class, room.getId());
			if (ref == null)
				throw new RoomNotFoundException();
			entityManager.remove(ref);
		} catch (RoomNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Delete a Receptionist
	 * 
	 * @param receptionist the Receptionist to delete
	 * @throws ReceptionistNotFoundException if the receptionist does not exist
	 * @throws DatabaseManagerException      in case of other errors
	 */
	public void deleteReceptionist(Receptionist receptionist)
			throws ReceptionistNotFoundException, DatabaseManagerException {
		try {
			beginTransaction();
			Receptionist ref = entityManager.find(Receptionist.class, receptionist.getId());
			if (ref == null)
				throw new ReceptionistNotFoundException();
			entityManager.remove(ref);
		} catch (ReceptionistNotFoundException e) {
			throw e;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Return a reservation given the id
	 * 
	 * @param id the unique id of a reservation
	 * @return the reservation
	 * @throws ReservationNotFoundException if the reservation does not exist
	 * @throws DatabaseManagerException     in case of other errors
	 */
	public Reservation retrieveReservation(Long id) throws ReservationNotFoundException, DatabaseManagerException {
		Reservation reservation = null;

		try {
			beginTransaction();
			reservation = entityManager.find(Reservation.class, id);
			if (reservation == null)
				throw new ReservationNotFoundException(id.toString());
			return reservation;
		} catch (ReservationNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/* OK */
	/**
	 * Return a reservation given the hotelId, the roomNumber and the date of
	 * check-in
	 * 
	 * @param hotelId
	 * @param room
	 * @param checkInDate
	 * @return the reservation
	 * @throws ReservationNotFoundException if the reservation does not exist
	 * @throws DatabaseManagerException     in case of errors
	 */
	public Reservation retrieveReservation(long hotelId, int room, Date checkInDate)
			throws ReservationNotFoundException, DatabaseManagerException {
		Reservation reservation = null;
		try {
			beginTransaction();
			reservation = entityManager
					.createNamedQuery("Reservation.getByHotelAndRoomAndCheckInDate", Reservation.class)
					.setParameter("hotelId", hotelId).setParameter("roomNumber", room)
					.setParameter("checkInDate", checkInDate, TemporalType.DATE).getSingleResult();
			return reservation;
		} catch (NoResultException nr) {
			throw new ReservationNotFoundException();
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/*
	 * /** Utility function to populate the database
	 * 
	 * @param manager
	 */
	public static void populateDatabase(DatabaseManager manager) {
		try {
			manager.insertCustomer(new Customer("federico", "pwd", "Federico", "Verdi"));
			Customer alessio = new Customer("alessio", "pwd", "Alessio", "Rossi");
			manager.insertCustomer(alessio);
			manager.insertCustomer(new Customer("chiara", "pwd", "Chiara", "Azzurri"));
			manager.insertCustomer(new Customer("marco", "pwd", "Marco", "Bianchi"));
			manager.insertCustomer(new Customer("luca", "pwd", "Luca", "Marroni"));
			manager.insertCustomer(new Customer("sara", "pwd", "Sara", "Violi"));
			manager.insertCustomer(new Customer("ettore", "pwd", "Ettore", "Amaranti"));
			Customer james = new Customer("james", "pwd", "James", "Blue");
			manager.insertCustomer(james);
			manager.insertCustomer(new Customer("nathan", "pwd", "Nathan", "Black"));
			manager.insertCustomer(new Customer("chloe", "pwd", "Chloe", "Red"));
			Customer ellie = new Customer("ellie", "pwd", "Ellie", "Green");
			manager.insertCustomer(ellie);
			manager.insertCustomer(new Customer("ellie2", "pwd", "Ellie", "Pink"));
			manager.insertCustomer(new Customer("sarah", "pwd", "Sarah", "Yellow"));
			Customer max = new Customer("max", "pwd", "Max", "Brown");
			manager.insertCustomer(max);
			Customer julia = new Customer("julia", "pwd", "Julia", "White");
			manager.insertCustomer(julia);
			Customer john = new Customer("john", "pwd", "John", "Orange");
			manager.insertCustomer(john);
			manager.insertCustomer(new Customer("luke", "pwd", "Luke", "Tan"));
			Customer kevin = new Customer("kevin", "pwd", "Kevin", "Purple");
			manager.insertCustomer(kevin);

			Hotel hotelRoma = new Hotel("Via Roma 26, Roma");
			manager.insertHotel(hotelRoma);
			Hotel hotelMilano = new Hotel("Via Milano 27, Milano");
			manager.insertHotel(hotelMilano);
			Hotel hotelBologna = new Hotel("Via Bologna 28, Bologna");
			manager.insertHotel(hotelBologna);
			Hotel hotelFirenze = new Hotel("Via Firenze 29, Firenze");
			manager.insertHotel(hotelFirenze);
			Hotel hotelPisa = new Hotel("Via Pisa 28, Pisa");
			manager.insertHotel(hotelPisa);

			manager.insertReceptionist(new Receptionist("r1", "pwd", "Laura", "Romani", hotelRoma));
			manager.insertReceptionist(new Receptionist("r2", "pwd", "Francesco", "Bolognesi", hotelBologna));
			manager.insertReceptionist(new Receptionist("r3", "pwd", "Mirco", "Rossi", hotelBologna));
			manager.insertReceptionist(new Receptionist("r4", "pwd", "Luisa", "Milanelli", hotelMilano));
			manager.insertReceptionist(new Receptionist("r5", "pwd", "Benedetta", "Vinci", hotelMilano));
			manager.insertReceptionist(new Receptionist("r6", "pwd", "Marco", "Duomo", hotelFirenze));
			manager.insertReceptionist(new Receptionist("r7", "pwd", "Benedetta", "Uffizi", hotelFirenze));
			manager.insertReceptionist(new Receptionist("r8", "pwd", "Lorena", "Duomo", hotelPisa));
			manager.insertReceptionist(new Receptionist("r9", "pwd", "Federico", "Lungarno", hotelPisa));

			manager.insertRoom(new Room(101, 2, hotelRoma));
			manager.insertRoom(new Room(102, 3, hotelRoma));
			manager.insertRoom(new Room(103, 2, hotelRoma));

			manager.insertRoom(new Room(101, 2, hotelMilano));
			manager.insertRoom(new Room(102, 3, hotelMilano));
			manager.insertRoom(new Room(201, 4, hotelMilano));

			manager.insertRoom(new Room(101, 4, hotelBologna));
			manager.insertRoom(new Room(201, 3, hotelBologna));
			manager.insertRoom(new Room(301, 2, hotelBologna));
			manager.insertRoom(new Room(302, 2, hotelBologna, false));

			manager.insertRoom(new Room(101, 4, hotelFirenze));
			manager.insertRoom(new Room(102, 3, hotelFirenze));
			manager.insertRoom(new Room(103, 2, hotelFirenze));
			manager.insertRoom(new Room(104, 2, hotelFirenze, false));

			manager.insertRoom(new Room(101, 4, hotelPisa));
			manager.insertRoom(new Room(201, 3, hotelPisa));
			manager.insertRoom(new Room(202, 2, hotelPisa));
			manager.insertRoom(new Room(301, 2, hotelPisa));

			Customer customer401 = new Customer("piergiorgio", "pwd", "Piergiorgio", "Neri");
			manager.insertCustomer(customer401);

			manager.insertRoom(new Room(401, 5, hotelBologna));

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			Date checkIn = sdf.parse("2019-11-15");
			Date checkOut = sdf.parse("2019-11-19");
			manager.insertReservation(3, 401, "piergiorgio", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2018-11-15");
			checkOut = sdf.parse("2018-11-19");
			manager.insertReservation(3, 401, "piergiorgio", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2019-01-15");
			checkOut = sdf.parse("2019-01-16");
			manager.insertReservation(5, 101, "max", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2019-02-26");
			checkOut = sdf.parse("2019-03-01");
			manager.insertReservation(5, 101, "ellie", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2020-02-26");
			checkOut = sdf.parse("2020-03-01");
			manager.insertReservation(2, 101, "ellie", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2020-02-12");
			checkOut = sdf.parse("2020-02-13");
			manager.insertReservation(2, 101, "john", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2019-12-20");
			checkOut = sdf.parse("2019-12-23");
			manager.insertReservation(2, 101, "john", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2019-12-20");
			checkOut = sdf.parse("2019-12-23");
			manager.insertReservation(5, 202, "kevin", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2020-09-28");
			checkOut = sdf.parse("2020-10-02");
			manager.insertReservation(5, 202, "ellie", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2019-10-01");
			checkOut = sdf.parse("2019-10-02");
			manager.insertReservation(2, 101, "james", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2019-10-14");
			checkOut = sdf.parse("2019-10-17");
			manager.insertReservation(5, 202, "james", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2020-06-04");
			checkOut = sdf.parse("2020-06-07");
			manager.insertReservation(5, 101, "kevin", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2020-07-04");
			checkOut = sdf.parse("2020-07-07");
			manager.insertReservation(5, 101, "julia", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2020-07-11");
			checkOut = sdf.parse("2020-07-21");
			manager.insertReservation(5, 202, "julia", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2020-07-23");
			checkOut = sdf.parse("2020-07-27");
			manager.insertReservation(2, 101, "julia", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2020-07-24");
			checkOut = sdf.parse("2020-07-27");
			manager.insertReservation(4, 102, "kevin", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2020-01-11");
			checkOut = sdf.parse("2020-01-14");
			manager.insertReservation(4, 102, "julia", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2019-08-11");
			checkOut = sdf.parse("2019-08-14");
			manager.insertReservation(1, 101, "julia", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2019-08-23");
			checkOut = sdf.parse("2019-09-02");
			manager.insertReservation(1, 101, "kevin", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2020-09-02");
			checkOut = sdf.parse("2020-09-03");
			manager.insertReservation(1, 101, "kevin", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2020-09-07");
			checkOut = sdf.parse("2020-09-09");
			manager.insertReservation(3, 301, "alessio", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2018-10-25");
			checkOut = sdf.parse("2018-11-01");
			manager.insertReservation(3, 301, "alessio", new Reservation(null, checkIn, checkOut, null));

			checkIn = sdf.parse("2019-06-07");
			checkOut = sdf.parse("2019-06-10");
			manager.insertReservation(3, 301, "alessio", new Reservation(null, checkIn, checkOut, null));

		} catch (CustomerUsernameAlreadyPresentException ex) {
			System.out.println(ex.getMessage() + " already present (customer)");
		} catch (ReceptionistUsernameAlreadyPresentException ex) {
			System.out.println(ex.getMessage() + " already present (receptionist)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}