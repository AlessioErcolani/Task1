package task1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.persistence.*;

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

	private void mergeObject(Object obj) {
		entityManager.merge(obj);
	}

	private void close() {
		entityManager.close();
	}

	/**
	 * Inserts a Customer in the database
	 * 
	 * @param customer the Customer to add
	 * @throws CustomerUsernameAlreadyPresentException if the username is already
	 *                                                 used
	 * @throws DatabaseManagerException
	 */
	public void addCustomer(Customer customer)
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
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * Inserts a Receptionist in the database
	 * 
	 * @param receptionist the Receptionist to add
	 * @throws ReceptionistUsernameAlreadyPresentException if the username is
	 *                                                     already used
	 * @throws DatabaseManagerException
	 */
	public void addReceptionist(Receptionist receptionist)
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
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * 
	 * @param hotel the hotel to add
	 * @throws HotelAlreadyPresentException is the address of the hotel is already
	 *                                      used
	 * @throws DatabaseManagerException     in case of errors
	 */
	public void addHotel(Hotel hotel) throws HotelAlreadyPresentException, DatabaseManagerException {
		try {
			beginTransaction();
			persistObject(hotel);
		} catch (PersistenceException ex) {
			throw new HotelAlreadyPresentException(ex.getMessage());
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * Inserts a room of the given hotel in the database. Note that this method does
	 * set the hotel field of the Room Object.
	 * 
	 * @param hotel the Hotel of the room
	 * @param room  the Room to add
	 * @throws DatabaseManagerException in case of errors
	 */
	public void addRoom(Room room) throws RoomAlreadyPresentException, DatabaseManagerException {
		try {
			beginTransaction();
			Hotel hotel = entityManager.find(Hotel.class, room.getHotel().getId());
			hotel.addRoom(room);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			try {
				commitTransaction();
			} catch (RollbackException ex) {
				throw new RoomAlreadyPresentException(ex.getMessage());
			}
			close();
		}
	}

	/**
	 * Inserts a reservation
	 * 
	 * @param reservation the reservation to add
	 * @throws DatabaseManagerException           in case of errors
	 * @throws ReservationAlreadyPresentException if the reservation is already
	 *                                            present
	 */
	public void addReservation(Reservation reservation)
			throws DatabaseManagerException, ReservationAlreadyPresentException {
		try {
			beginTransaction();
			Room room = entityManager.find(Room.class, reservation.getRoom().getId());
			// add to the SQL database
			room.addReservation(reservation);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			try {
				commitTransaction();

				// simulation key-value down
				if (keyValue.isAvailable) {

					// when add in the SQL database terminates successfully, add in the key-value
					// database
					new Thread(new Runnable() {
						@Override
						public void run() {
							Booking booking = new Booking(reservation.getCustomer().getName(),
									reservation.getCustomer().getSurname(),
									Integer.toString(reservation.getRoom().getNumber()));
							try {
								keyValue.insertBooking(Long.toString(reservation.getId()), booking);
							} catch (KeyValueDatabaseManagerException | BookingAlreadyPresentException e) {
								System.out.println("Add on key value\n");
								String error = "Error in writing reservation for " + booking.getName() + " "
										+ booking.getSurname() + " in room " + booking.getRoomNumber() + "\n";
								writeErrorLog("[ERR_INSERT]: " + error + "\n");
							}
						}
					}).start();
				} else {
					writeErrorLog("[INSERT]: " + new Booking(reservation.getCustomer().getName(),
							reservation.getCustomer().getSurname(), Integer.toString(reservation.getRoom().getNumber()))
							+ "\n");
				}
			} catch (RollbackException ex) {
				throw new ReservationAlreadyPresentException(ex.getMessage());
			}
			close();
		}
	}

	public void insertReservation(long hotelId, int roomNumber, String username, Reservation reservation)
			throws RoomNotFoundException, RoomAlreadyBookedException, CustomerNotFoundException, DatabaseManagerException, ReservationAlreadyPresentException {
		try {
			beginTransaction();

			Room room;
			try {
				// read the room
				room = entityManager.createNamedQuery("Room.findByHotelAndNumber", Room.class)
						.setParameter("hotelId", hotelId).setParameter("roomNumber", roomNumber).getSingleResult();
			} catch (NoResultException nr) {
				throw new RoomNotFoundException();
			}
			
			// check if room is reservable
			List<Room> reservableRooms = entityManager.createNamedQuery("Room.getReservableRoomsGivenPeriod", Room.class)
					.setParameter("hotelId", hotelId).setParameter("startPeriod", reservation.getCheckInDate())
					.setParameter("endPeriod", reservation.getCheckOutDate()).getResultList();
			if (!reservableRooms.contains(room))
				throw new RoomAlreadyBookedException();
			
			Customer customer;
			try {
				// read the customer
				customer = entityManager.createNamedQuery("Customer.findByUsername", Customer.class)
						.setParameter("username", username).getSingleResult();
			} catch (NoResultException nr) {
				throw new CustomerNotFoundException();
			}
		
			// add reservation
			reservation.setRoom(room);
			reservation.setCustomer(customer);
			room.addReservation(reservation);
		} catch (RoomNotFoundException | RoomAlreadyBookedException | CustomerNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new DatabaseManagerException();
		} finally {
			try {
				commitTransaction();

				// simulation key-value down
				if (keyValue.isAvailable) {

					// when add in the SQL database terminates successfully, add in the key-value
					// database
					new Thread(new Runnable() {
						@Override
						public void run() {
							Booking booking = new Booking(reservation.getCustomer().getName(),
									reservation.getCustomer().getSurname(),
									Integer.toString(reservation.getRoom().getNumber()));
							try {
								keyValue.insertBooking(Long.toString(reservation.getId()), booking);
							} catch (KeyValueDatabaseManagerException | BookingAlreadyPresentException e) {
								System.out.println("Add on key value\n");
								String error = "Error in writing reservation for " + booking.getName() + " "
										+ booking.getSurname() + " in room " + booking.getRoomNumber() + "\n";
								writeErrorLog("[ERR_INSERT]: " + error + "\n");
							}
						}
					}).start();
				} else {
					writeErrorLog("[INSERT]: " + new Booking(reservation.getCustomer().getName(),
							reservation.getCustomer().getSurname(), Integer.toString(reservation.getRoom().getNumber()))
							+ "\n");
				}
			} catch (RollbackException ex) {
				throw new ReservationAlreadyPresentException(ex.getMessage());
			}
			close();
		}
	}

	public void writeErrorLog(String error) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("errorLog.txt"), true));
			writer.append(error);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update a reservation
	 * 
	 * @param oldReservation
	 * @param newReservation is the new reservation
	 * @throws DatabaseManagerException in case of errors
	 */
	public void updateReservation(Reservation oldReservation, Reservation newReservation)
			throws DatabaseManagerException {
		try {
			beginTransaction();
			Room oldRoom = entityManager.find(Room.class, oldReservation.getRoom().getId());
			oldRoom.removeReservation(oldReservation);

			entityManager.flush();

			Room newRoom = entityManager.find(Room.class, newReservation.getRoom().getId());
			newRoom.addReservation(newReservation);

		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			try {
				commitTransaction();

				// used to simulate the key-value down
				if (keyValue.isAvailable) {

					keyValue.deleteBooking(Long.toString(oldReservation.getId()));

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
				} else {
					writeErrorLog("[UPDATE]: " + new Booking(newReservation.getCustomer().getName(),
							newReservation.getCustomer().getSurname(),
							Integer.toString(newReservation.getRoom().getNumber())) + "\n");
				}

			} catch (RollbackException ex) {
				throw new DatabaseManagerException(ex.getMessage());
			}
			close();
		}
	}

	/**
	 * Delete a reservation
	 * 
	 * @param reservation the Reservation to delete
	 * @throws DatabaseManagerException in case of errors
	 */
	public void deleteReservation(Reservation reservationToDelete) throws DatabaseManagerException {
		try {
			beginTransaction();
			Reservation reservation = entityManager.find(Reservation.class, reservationToDelete.getId());
			entityManager.remove(reservation);

			// delete key-value pair
			if (!keyValue.isAvailable) {
				keyValue.deleteBooking(Long.toString(reservation.getId()));
				writeErrorLog("[DELETE]: " + Long.toString(reservation.getId()) + "\n");
			}
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * Get the list of reservations for a customer
	 * 
	 * @param customer
	 * @return the list of reservations
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Reservation> retrieveUpcomingCustomerReservations(Customer customer) throws DatabaseManagerException {
		try {
			beginTransaction();
			List<Reservation> upcomingReservations = entityManager
					.createNamedQuery("Reservation.getByCustomer", Reservation.class)
					.setParameter("customerId", customer.getId()).getResultList();
			return upcomingReservations;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * Get the list of upcoming reservations for the given hotel and check-in date
	 * 
	 * @param hotel
	 * @param date  the minimum starting date for the check-in
	 * @return the list of reservations
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Reservation> getUpcomingReservations(Hotel hotel, Date date) throws DatabaseManagerException {
		try {
			beginTransaction();
			List<Reservation> upcomingReservations = entityManager
					.createNamedQuery("Reservation.getByHotel", Reservation.class)
					.setParameter("hotelId", hotel.getId()).setParameter("from", date, TemporalType.DATE)
					.getResultList();
			return upcomingReservations;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * Get the list of rooms of an hotel that are bookable in a given period, i.e.
	 * they are available AND not occupied in the period
	 * 
	 * @param hotel       is the Hotel of the room
	 * @param startPeriod is the start date of the period
	 * @param endPeriod   is the end date of the period
	 * @return a list of available rooms
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Room> getReservableRooms(Hotel hotel, Date startPeriod, Date endPeriod)
			throws DatabaseManagerException {
		try {
			beginTransaction();
			List<Room> rooms = entityManager.createNamedQuery("Room.getReservableRoomsGivenPeriod", Room.class)
					.setParameter("hotelId", hotel.getId()).setParameter("startPeriod", startPeriod)
					.setParameter("endPeriod", endPeriod).getResultList();
			return rooms;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	public List<Room> retrieveReservableRooms(Long hotelId, Date startPeriod, Date endPeriod)
			throws DatabaseManagerException, HotelNotFoundException {
		try {
			beginTransaction();

			Hotel hotel = entityManager.find(Hotel.class, hotelId);
			if (hotel == null)
				throw new HotelNotFoundException(hotelId.toString());

			List<Room> rooms = entityManager.createNamedQuery("Room.getReservableRoomsGivenPeriod", Room.class)
					.setParameter("hotelId", hotel.getId()).setParameter("startPeriod", startPeriod)
					.setParameter("endPeriod", endPeriod).getResultList();

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

	/**
	 * Get the list of rooms of an hotel that are unreservable in a given period,
	 * i.e. they are unavailable OR occupied in the given period
	 * 
	 * @param hotel       is the Hotel of the room
	 * @param startPeriod is the start date of the period
	 * @param endPeriod   is the end date of the period
	 * @return a list of available rooms
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Room> getUnreservableRooms(Hotel hotel, Date startPeriod, Date endPeriod)
			throws DatabaseManagerException {
		try {
			beginTransaction();
			TypedQuery<Room> query = entityManager.createNamedQuery("Room.getUnreservableRoomsGivenPeriod", Room.class);
			query.setParameter("hotelId", hotel.getId());
			query.setParameter("startPeriod", startPeriod);
			query.setParameter("endPeriod", endPeriod);
			List<Room> rooms = query.getResultList();
			return rooms;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	public List<Room> retrieveUnreservableRooms(Long hotelId, Date startPeriod, Date endPeriod)
			throws DatabaseManagerException {
		try {
			beginTransaction();
			
			Hotel hotel = entityManager.find(Hotel.class, hotelId);
			if (hotel == null)
				throw new HotelNotFoundException(hotelId.toString());

			List<Room> rooms = entityManager.createNamedQuery("Room.getUnreservableRoomsGivenPeriod", Room.class)
					.setParameter("hotelId", hotel.getId()).setParameter("startPeriod", startPeriod)
					.setParameter("endPeriod", endPeriod).getResultList();
			return rooms;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * Set a room in an hotel as available
	 * 
	 * @param unavailableRoom
	 * @return the updated room
	 * @throws DatabaseManagerException in case of errors
	 */
	public Room setRoomAvailable(Room unavailableRoom) throws DatabaseManagerException {
		try {
			beginTransaction();
			Room room = entityManager.find(Room.class, unavailableRoom.getId());
			room.setAvailable(true);
			return room;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * Set a room in an hotel as unavailable
	 * 
	 * @param availableRoom is the number of the room
	 * @return the updated room
	 * @throws DatabaseManagerException in case of errors
	 */
	public Room setRoomUnavailable(Room availableRoom) throws DatabaseManagerException {
		try {
			beginTransaction();
			Room room = entityManager.find(Room.class, availableRoom.getId());
			room.setAvailable(false);
			return room;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

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

	/**
	 * Return a list of the hotels
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

	/**
	 * Return an hotel given the address
	 * 
	 * @param address of the hotel
	 * @return the hotel
	 * @throws HotelNotFoundException   if the hotel does not exist
	 * @throws DatabaseManagerException in case of errors
	 */
	public Hotel readHotel(String address) throws HotelNotFoundException, DatabaseManagerException {
		Hotel hotel = null;
		try {
			beginTransaction();
			TypedQuery<Hotel> query = entityManager.createNamedQuery("Hotel.findByAddress", Hotel.class);
			query.setParameter("address", address);
			hotel = query.getSingleResult();
		} catch (NoResultException nr) {
			throw new HotelNotFoundException();
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
		return hotel;
	}

	/**
	 * Return an hotel given an id
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

	/**
	 * Return the list of the rooms for a specific hotel
	 * 
	 * @param hotel
	 * @return the list of the rooms of the hotels
	 * @throws DatabaseManagerException in case of errors
	 */
	public List<Room> getRoomsOfHotel(Hotel hotel) throws DatabaseManagerException {
		try {
			beginTransaction();
			List<Room> rooms = entityManager.createNamedQuery("Room.findByHotel", Room.class)
					.setParameter("hotelId", hotel.getId()).getResultList();
			return rooms;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

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

	/**
	 * Return a room given an hotelId and the room number
	 * 
	 * @param hotelId    the unique id of the hotel
	 * @param roomNumber
	 * @return the room
	 * @throws DatabaseManagerException in case of errors
	 * @throws RoomNotFoundException    if the room does not exist
	 */
	public Room readRoom(long hotelId, int roomNumber) throws DatabaseManagerException, RoomNotFoundException {
		Room room = null;
		try {
			beginTransaction();
			TypedQuery<Room> query = entityManager.createNamedQuery("Room.findByHotelAndNumber", Room.class);
			query.setParameter("hotelId", hotelId);
			query.setParameter("roomNumber", roomNumber);
			room = query.getSingleResult();
		} catch (NoResultException nr) {
			throw new RoomNotFoundException();
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
		return room;
	}

	/**
	 * Return a customer given the username
	 * 
	 * @param username
	 * @return the customer
	 * @throws DatabaseManagerException  in case of errors
	 * @throws CustomerNotFoundException if the customer with that username does not
	 *                                   exist
	 */
	public Customer readCustomer(String username) throws DatabaseManagerException, CustomerNotFoundException {
		Customer customer = null;
		try {
			beginTransaction();
			TypedQuery<Customer> query = entityManager.createNamedQuery("Customer.findByUsername", Customer.class);
			query.setParameter("username", username);
			customer = query.getSingleResult();
		} catch (NoResultException nr) {
			throw new CustomerNotFoundException(username);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
		return customer;
	}

	/**
	 * Return a receptionist given the username
	 * 
	 * @param username
	 * @return the receptionist
	 * @throws DatabaseManagerException      in case of errors
	 * @throws ReceptionistNotFoundException if the receptionist with that username
	 *                                       does not exist
	 */
	public Receptionist readReceptionist(String username)
			throws DatabaseManagerException, ReceptionistNotFoundException {
		Receptionist receptionist = null;
		try {
			beginTransaction();
			TypedQuery<Receptionist> query = entityManager.createNamedQuery("Receptionist.findByUsername",
					Receptionist.class);
			query.setParameter("username", username);
			receptionist = query.getSingleResult();
		} catch (NoResultException nr) {
			throw new ReceptionistNotFoundException(username);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
		return receptionist;
	}

	/**
	 * Return a reservation given the hotelId, the roomNumber and the date of
	 * check-in
	 * 
	 * @param hotelId
	 * @param room
	 * @param checkInDate
	 * @return the reservation
	 * @throws DatabaseManagerException     in case of errors
	 * @throws ReservationNotFoundException if the reservation does not exist
	 */
	public Reservation readReservation(long hotelId, int room, Date checkInDate)
			throws DatabaseManagerException, ReservationNotFoundException {
		Reservation reservation = null;
		try {
			beginTransaction();
			TypedQuery<Reservation> query = entityManager.createNamedQuery("Reservation.getByHoteAndRoomAndCheckInDate",
					Reservation.class);
			query.setParameter("hotelId", hotelId);
			query.setParameter("roomNumber", room);
			query.setParameter("checkInDate", checkInDate);
			reservation = query.getSingleResult();
		} catch (NoResultException nr) {
			throw new ReservationNotFoundException();
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
		return reservation;
	}

	/**
	 * Delete a customer
	 * 
	 * @param customer the customer to delete
	 * @throws DatabaseManagerException in case of errors
	 */
	public void deleteCustomer(Customer customer) throws DatabaseManagerException {
		try {
			beginTransaction();
			Customer ref = entityManager.find(Customer.class, customer.getId());
			entityManager.remove(ref);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * Delete an hotel
	 * 
	 * @param hotel the hotel to delete
	 * @throws DatabaseManagerException in case of errors
	 */
	public void deleteHotel(Hotel hotel) throws DatabaseManagerException {
		try {
			beginTransaction();
			Hotel ref = entityManager.find(Hotel.class, hotel.getId());
			entityManager.remove(ref);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * Delete a room
	 * 
	 * @param room the room to delete
	 * @throws DatabaseManagerException in case of errors
	 */

	public void deleteRoom(Room room) throws DatabaseManagerException {
		try {
			beginTransaction();
			Room ref = entityManager.find(Room.class, room.getId());
			entityManager.remove(ref);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * Delete a receptionist
	 * 
	 * @param receptionist the receptionist to delete
	 * @throws DatabaseManagerException in case of errors
	 */
	public void deleteReceptionist(Receptionist receptionist) throws DatabaseManagerException {
		try {
			beginTransaction();
			Receptionist ref = entityManager.find(Receptionist.class, receptionist.getId());
			entityManager.remove(ref);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * Return a reservation given the id
	 * 
	 * @param id the unique id of a reservation
	 * @return the reservation
	 * @throws ReservationNotFoundException if the reservation does not exist
	 * @throws DatabaseManagerException     in case of error
	 */
	public Reservation getReservation(Long id) throws ReservationNotFoundException, DatabaseManagerException {
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

	/**
	 * Update a room
	 * 
	 * @param room
	 * @throws DatabaseManagerException in case of errors
	 */
	public void updateRoom(Room room) throws DatabaseManagerException {
		try {
			beginTransaction();
			mergeObject(room);
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			commitTransaction();
			close();
		}
	}

	/**
	 * Utility function to populate the database
	 * 
	 * @param manager
	 */
	public static void populateDatabase(DatabaseManager manager) {
		try {
			manager.addCustomer(new Customer("federico", "pwd", "Federico", "Verdi"));
			Customer alessio = new Customer("alessio", "pwd", "Alessio", "Rossi");
			manager.addCustomer(alessio);
			manager.addCustomer(new Customer("chiara", "pwd", "Chiara", "Azzurri"));
			manager.addCustomer(new Customer("marco", "pwd", "Marco", "Bianchi"));
			manager.addCustomer(new Customer("luca", "pwd", "Luca", "Marroni"));
			manager.addCustomer(new Customer("sara", "pwd", "Sara", "Violi"));
			manager.addCustomer(new Customer("ettore", "pwd", "Ettore", "Amaranti"));
			Customer james = new Customer("james", "pwd", "James", "Blue");
			manager.addCustomer(james);
			manager.addCustomer(new Customer("nathan", "pwd", "Nathan", "Black"));
			manager.addCustomer(new Customer("chloe", "pwd", "Chloe", "Red"));
			Customer ellie = new Customer("ellie", "pwd", "Ellie", "Green");
			manager.addCustomer(ellie);
			manager.addCustomer(new Customer("ellie2", "pwd", "Ellie", "Pink"));
			manager.addCustomer(new Customer("sarah", "pwd", "Sarah", "Yellow"));
			Customer max = new Customer("max", "pwd", "Max", "Brown");
			manager.addCustomer(max);
			Customer julia = new Customer("julia", "pwd", "Julia", "White");
			manager.addCustomer(julia);
			Customer john = new Customer("john", "pwd", "John", "Orange");
			manager.addCustomer(john);
			manager.addCustomer(new Customer("luke", "pwd", "Luke", "Tan"));
			Customer kevin = new Customer("kevin", "pwd", "Kevin", "Purple");
			manager.addCustomer(kevin);

			Hotel hotelRoma = new Hotel("Via Roma 26, Roma");
			manager.addHotel(hotelRoma);
			Hotel hotelMilano = new Hotel("Via Milano 27, Milano");
			manager.addHotel(hotelMilano);
			Hotel hotelBologna = new Hotel("Via Bologna 28, Bologna");
			manager.addHotel(hotelBologna);
			Hotel hotelFirenze = new Hotel("Via Firenze 29, Firenze");
			manager.addHotel(hotelFirenze);
			Hotel hotelPisa = new Hotel("Via Pisa 28, Pisa");
			manager.addHotel(hotelPisa);

			manager.addReceptionist(new Receptionist("r1", "pwd", "Laura", "Romani", hotelRoma));
			manager.addReceptionist(new Receptionist("r2", "pwd", "Francesco", "Bolognesi", hotelBologna));
			manager.addReceptionist(new Receptionist("r3", "pwd", "Mirco", "Rossi", hotelBologna));
			manager.addReceptionist(new Receptionist("r4", "pwd", "Luisa", "Milanelli", hotelMilano));
			manager.addReceptionist(new Receptionist("r5", "pwd", "Benedetta", "Vinci", hotelMilano));
			manager.addReceptionist(new Receptionist("r6", "pwd", "Marco", "Duomo", hotelFirenze));
			manager.addReceptionist(new Receptionist("r7", "pwd", "Benedetta", "Uffizi", hotelFirenze));
			manager.addReceptionist(new Receptionist("r8", "pwd", "Lorena", "Duomo", hotelPisa));
			manager.addReceptionist(new Receptionist("r9", "pwd", "Federico", "Lungarno", hotelPisa));
 
			manager.addRoom(new Room(101, 2, hotelRoma));
			manager.addRoom(new Room(102, 3, hotelRoma));
			manager.addRoom(new Room(103, 2, hotelRoma));

			manager.addRoom(new Room(101, 2, hotelMilano));
			manager.addRoom(new Room(102, 3, hotelMilano));
			manager.addRoom(new Room(201, 4, hotelMilano));

			manager.addRoom(new Room(101, 4, hotelBologna));
			manager.addRoom(new Room(201, 3, hotelBologna));
			manager.addRoom(new Room(301, 2, hotelBologna));
			manager.addRoom(new Room(302, 2, hotelBologna, false));

			manager.addRoom(new Room(101, 4, hotelFirenze));
			manager.addRoom(new Room(102, 3, hotelFirenze));
			manager.addRoom(new Room(103, 2, hotelFirenze));
			manager.addRoom(new Room(104, 2, hotelFirenze, false));

			manager.addRoom(new Room(101, 4, hotelPisa));
			manager.addRoom(new Room(201, 3, hotelPisa));
			manager.addRoom(new Room(202, 2, hotelPisa));
			manager.addRoom(new Room(301, 2, hotelPisa));

			Customer customer401 = new Customer("piergiorgio", "pwd", "Piergiorgio", "Neri");
			manager.addRoom(new Room(401, 5, hotelBologna));
			manager.addCustomer(customer401);

			Calendar calendar = Calendar.getInstance();
			calendar.set(2019, 11 - 1, 15, 1, 0, 0);
			Date checkIn = calendar.getTime();

			calendar.set(2019, 11 - 1, 19, 1, 0, 0);
			Date checkOut = calendar.getTime();

			manager.insertReservation(3, 401, "piergiorgio", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2018, 11 - 1, 15, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2018, 11 - 1, 19, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(3, 401, "piergiorgio", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2019, Calendar.JANUARY, 15, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2019, Calendar.JANUARY, 16, 1, 0, 0);
			checkOut = calendar.getTime();
			
			manager.insertReservation(5, 101, "max", new Reservation(null, checkIn, checkOut, null));
			
			calendar.set(2019, Calendar.FEBRUARY, 26, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2019, Calendar.MARCH, 1, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(5, 101, "ellie", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2020, Calendar.FEBRUARY, 26, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2020, Calendar.MARCH, 1, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(2, 101, "ellie", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2020, Calendar.FEBRUARY, 12, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2020, Calendar.FEBRUARY, 13, 1, 0, 0);
			checkOut = calendar.getTime();
			
			manager.insertReservation(2, 101, "john", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2019, Calendar.DECEMBER, 20, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2019, Calendar.DECEMBER, 23, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(2, 101, "john", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2019, Calendar.DECEMBER, 20, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2019, Calendar.DECEMBER, 23, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(5, 202, "kevin", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2020, Calendar.SEPTEMBER, 28, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2020, Calendar.OCTOBER, 2, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(5, 202, "ellie", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2019, Calendar.OCTOBER, 1, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2019, Calendar.OCTOBER, 2, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(2, 101, "james", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2019, Calendar.OCTOBER, 14, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2019, Calendar.OCTOBER, 17, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(5, 202, "james", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2020, Calendar.JUNE, 4, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2020, Calendar.JUNE, 7, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(5, 101, "kevin", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2020, Calendar.JULY, 4, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2020, Calendar.JULY, 7, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(5, 101, "julia", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2020, Calendar.JULY, 11, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2020, Calendar.JULY, 21, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(5, 202, "julia", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2020, Calendar.JULY, 23, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2020, Calendar.JULY, 27, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(2, 101, "julia", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2020, Calendar.JULY, 24, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2020, Calendar.JULY, 27, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(4, 102, "kevin", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2020, Calendar.JANUARY, 11, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2020, Calendar.JANUARY, 14, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(4, 102, "julia", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2019, Calendar.AUGUST, 11, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2019, Calendar.AUGUST, 14, 1, 0, 0);
			checkOut = calendar.getTime();
			
			manager.insertReservation(1, 101, "julia", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2019, Calendar.AUGUST, 23, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2019, Calendar.SEPTEMBER, 2, 1, 0, 0);
			checkOut = calendar.getTime();
			
			manager.insertReservation(1, 101, "kevin", new Reservation(null, checkIn, checkOut, null));
			
			calendar.set(2020, Calendar.SEPTEMBER, 2, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2020, Calendar.SEPTEMBER, 3, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(1, 101, "kevin", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2020, Calendar.SEPTEMBER, 7, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2020, Calendar.SEPTEMBER, 9, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(3, 301, "alessio", new Reservation(null, checkIn, checkOut, null));

			calendar.set(2018, Calendar.OCTOBER, 25, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2018, Calendar.NOVEMBER, 1, 1, 0, 0);
			checkOut = calendar.getTime();

			manager.insertReservation(3, 301, "alessio", new Reservation(null, checkIn, checkOut, null));
			calendar.set(2019, Calendar.JUNE, 7, 1, 0, 0);
			checkIn = calendar.getTime();

			calendar.set(2019, Calendar.JUNE, 10, 1, 0, 0);
			checkOut = calendar.getTime();

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
