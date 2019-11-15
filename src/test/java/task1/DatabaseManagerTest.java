package task1;

import static org.junit.Assert.*;
import java.util.*;
import java.util.logging.Level;
import org.junit.*;
import exc.*;

public class DatabaseManagerTest {

	private static DatabaseManager manager;

	@BeforeClass
	public static void setup() throws DatabaseManagerException {
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
		manager = new DatabaseManager("hotel_chain");
		// DatabaseManager.populateDatabase(manager);
	}

	@AfterClass
	public static void finish() {
		manager.exit();
	}

	@Test
	public void testAddAndReadCustomer() {
		// add new customer
		Customer customer = new Customer("username", "pwd", "name", "surname");
		try {
			manager.insertCustomer(customer);
		} catch (CustomerUsernameAlreadyPresentException e) {
			fail("Test add new customer: failed because username already present.");
		} catch (DatabaseManagerException e) {
			fail("Test add new customer: failed.");
		}

		// read the inserted customer
		Customer readCustomer = null;
		try {
			readCustomer = manager.readCustomer("username");
		} catch (CustomerNotFoundException e) {
			fail("Test read customer: failed because customer not found.");
		} catch (DatabaseManagerException e) {
			fail("Test read customer: failed.");
		}
		assertTrue("Test add new customer", customer.equals(readCustomer));

		// try to add customer with an username already present
		Customer customerCopy = new Customer("username", "newPwd", "newName", "newSurname");
		boolean exception = false;
		try {
			manager.insertCustomer(customerCopy);
		} catch (CustomerUsernameAlreadyPresentException e) {
			exception = true;
		} catch (DatabaseManagerException e) {
			fail("Test add new customer: failed.");
		}
		assertTrue("Test add new customer", exception);

		// authenticate the customer by username and password
		Customer authenticateCustomer = null;
		try {
			authenticateCustomer = manager.authenticateCustomer("username", "pwd");
		} catch (CustomerAuthenticationFailure e) {
			fail("Test authenticate customer: failed.");
		}
		assertTrue("Test authenticate customer", customer.equals(authenticateCustomer));

		// delete the customer
		try {
			manager.deleteCustomer(readCustomer);
		} catch (DatabaseManagerException e) {
			fail("Test delete customer: failed.");
		}

		exception = false;
		try {
			readCustomer = manager.readCustomer("username");
		} catch (CustomerNotFoundException e) {
			exception = true;
		} catch (DatabaseManagerException e) {
			fail("Test read deleted customer: failed.");
		}
		assertTrue("Test read deleted customer", exception);
	}

	@Test
	public void testAddAndReadHotelAndRoom() {
		String address = "Via Ferrara 45, Ferrara";

		// add new hotel
		Hotel hotel = new Hotel(address);
		try {
			manager.addHotel(hotel);
		} catch (HotelAlreadyPresentException e) {
			fail("Test add new hotel: failed because hotel already present.");
		} catch (DatabaseManagerException e) {
			fail("Test add new hotel: failed.");
		}

		// read hotel
		Hotel readHotel = null;
		try {
			readHotel = manager.readHotel(address);
		} catch (HotelNotFoundException e) {
			fail("Test read hotel: failed.");
		} catch (DatabaseManagerException e) {
			fail("Test read hotel: failed.");
		}
		assertTrue("Test read hotel.", hotel.equals(readHotel));

		// try to add again the hotel
		boolean exception = false;
		try {
			manager.addHotel(hotel);
		} catch (HotelAlreadyPresentException e) {
			exception = true;
		} catch (DatabaseManagerException e) {
			fail("Test add existing hotel: failed.");
		}
		assertTrue("Test add existing hotel.", exception);

		// add a room to the hotel
		Room room = new Room(101, 5, hotel);
		try {
			manager.addRoom(room);
		} catch (RoomAlreadyPresentException e) {
			fail("Test add room: failed because room already present.");
		} catch (DatabaseManagerException e) {
			fail("Test add room: failed.");
		}

		// read room
		Room readRoom = null;
		try {
			readRoom = manager.retrieveRoom(hotel.getId(), 101);
		} catch (RoomNotFoundException e) {
			fail("Test read room: failed.");
		} catch (DatabaseManagerException e) {
			fail("Test read room: failed.");
		}
		assertTrue("Test read room.", room.equals(readRoom));

		// try to add again the room
		exception = false;
		try {
			manager.addRoom(new Room(101, 5, hotel));
		} catch (RoomAlreadyPresentException e) {
			exception = true;
		} catch (DatabaseManagerException e) {
			fail("Test add room: failed.");
		}
		assertTrue("Test add existing room.", exception);

		// delete the inserted room
		exception = false;
		try {
			manager.deleteRoom(readRoom);
		} catch (DatabaseManagerException e) {
			fail("Test delete hotel: failed.");
		}

		// add again the room
		room = new Room(101, 5, hotel);
		try {
			manager.addRoom(room);
		} catch (RoomAlreadyPresentException e) {
			fail("Test add room: failed because room already present.");
		} catch (DatabaseManagerException e) {
			fail("Test add room: failed.");
		}

		// delete the inserted hotel
		exception = false;
		try {
			manager.deleteHotel(readHotel);
		} catch (DatabaseManagerException e) {
			fail("Test delete hotel: failed.");
		}

		// try to read the deleted hotel
		exception = false;
		try {
			readHotel = manager.readHotel(address);
		} catch (HotelNotFoundException e) {
			exception = true;
		} catch (DatabaseManagerException e) {
			fail("Test read deleted hotel: failed.");
		}
		assertTrue("Test read deleted hotel.", exception);

		// try to read the room deleted together with the hotel
		exception = false;
		try {
			readRoom = manager.retrieveRoom(hotel.getId(), 101);
		} catch (RoomNotFoundException e) {
			exception = true;
		} catch (DatabaseManagerException e) {
			fail("Test read unexisting room room: failed.");
		}
		assertTrue("Test read room.", exception);
	}

	@Test
	public void testAddAndReadReceptionist() {
		String address = "Via Ferrara 44, Ferrara";

		Hotel hotel = new Hotel(address);
		// add hotel
		try {
			manager.addHotel(hotel);
		} catch (DatabaseManagerException | HotelAlreadyPresentException e) {
			fail("Add hotel: failed.");
		}

		Receptionist receptionist = new Receptionist("username", "pwd", "name", "surname", hotel);
		try {
			manager.addReceptionist(receptionist);
		} catch (ReceptionistUsernameAlreadyPresentException e) {
			fail("Test add new receptionist: failed because username already present!");
		} catch (DatabaseManagerException e) {
			fail("Test add new receptionist: failed.");
		}

		// test add receptionist with an username already present
		Receptionist ReceptionistCopy = new Receptionist("username", "newPwd", "newName", "newSurname", hotel);
		boolean exception = false;
		try {
			manager.addReceptionist(ReceptionistCopy);
		} catch (ReceptionistUsernameAlreadyPresentException e) {
			exception = true;
		} catch (DatabaseManagerException e) {
			fail("Test add new receptionist: failed. ");
		}
		assertTrue("Test add existing receptionist.", exception);

		// test read receptionist by username and password
		Receptionist readReceptionist = null;
		try {
			readReceptionist = manager.authenticateReceptionist("username", "pwd");
		} catch (ReceptionistAuthenticationFailure e) {
			fail("Test authenticate receptionist: failed.");
		}
		assertEquals("Test authenticate receptionist.", receptionist, readReceptionist);

		// test delete a receptionist
		exception = false;
		try {
			manager.deleteReceptionist(readReceptionist);
		} catch (DatabaseManagerException e) {
			exception = true;
		}
		assertFalse("Test delete receptionist.", exception);

		// add again receptionist
		try {
			manager.addReceptionist(receptionist);
		} catch (ReceptionistUsernameAlreadyPresentException e) {
			fail("Test add new receptionist: failed because username already present!");
		} catch (DatabaseManagerException e) {
			fail("Test add new receptionist: failed.");
		}

		// delete an hotel
		try {
			manager.deleteHotel(hotel);
		} catch (DatabaseManagerException e) {
			fail("Delete hotel: failed.");
		}

		// try to read the receptionist (must be deleted with the hotel)
		exception = false;
		try {
			manager.readReceptionist("username");
		} catch (ReceptionistNotFoundException e) {
			exception = true;
		} catch (DatabaseManagerException e) {
			fail("Read receptionist: failed");
		}
		assertTrue("Test read deleted receptionist.", exception);
	}

	// Test for add, update and delete reservation
	@Test
	public void testAddUpdateAndDeleteReservation() {
		Hotel hotel = null;
		// read the hotel
		try {
			hotel = manager.readHotel("Via Bologna 28, Bologna");
		} catch (HotelNotFoundException | DatabaseManagerException e) {
			fail("Read hotel: failed.");
		}

		// add a new room
		Room room1 = new Room(105, 5, hotel);
		try {
			manager.addRoom(room1);
		} catch (DatabaseManagerException | RoomAlreadyPresentException e) {
			fail("Read room: failed.");
		}

		// add a new customer
		Customer customer1 = new Customer("username", "password", "name", "surname");
		try {
			manager.insertCustomer(customer1);
		} catch (DatabaseManagerException | CustomerUsernameAlreadyPresentException e) {
			fail("Read customer: failed.");
		}

		Calendar calendar = Calendar.getInstance();
		calendar.set(2020, 11 - 1, 6, 1, 0, 0);
		Date checkInDate1 = calendar.getTime();

		calendar.set(2020, 11 - 1, 11, 1, 0, 0);
		Date checkOutDate1 = calendar.getTime();

		// add a new reservation in the new room for the new customer
		try {
			manager.insertReservation(hotel.getId(), 105, "username",
					new Reservation(null, checkInDate1, checkOutDate1, null));
		} catch (RoomNotFoundException e) {
			fail("Test add new reservation: failed.");
		} catch (RoomAlreadyBookedException e) {
			fail("Test add new reservation: failed.");
		} catch (CustomerNotFoundException e) {
			fail("Test add new reservation: failed.");
		} catch (DatabaseManagerException e1) {
			fail("Test add new reservation: failed.");
		} catch (ReservationAlreadyPresentException e1) {
			fail("Test add new reservation: failed because reservation already present.");
			;
		}

		// read reservation
		Reservation readReservation = null;
		try {
			readReservation = manager.readReservation(hotel.getId(), 105, checkInDate1);
		} catch (ReservationNotFoundException e) {
			fail("Test read reservation: failed because reservation not found.");
		} catch (DatabaseManagerException e) {
			fail("Test read reservation: failed.");
		}

		// get all reservations of a customer
		List<Reservation> upcomingReservations = null;
		try {
			upcomingReservations = manager.retrieveUpcomingCustomerReservations(customer1);
		} catch (DatabaseManagerException e) {
			fail("Test get reservations of a customer: failed.");
		}

		// verify if the new reservation is correctly inserted
		assertTrue("Test: reservation inserted", upcomingReservations.contains(readReservation));

		// add another new room
		Room room2 = new Room(106, 6, hotel);
		try {
			manager.addRoom(room2);
		} catch (DatabaseManagerException | RoomAlreadyPresentException e) {
			fail("Read room: failed.");
		}

		// update reservation changing room
		Reservation newReservation = new Reservation(checkInDate1, checkOutDate1);

		try {
			manager.updateReservation(hotel.getId(), 105, checkInDate1, hotel.getId(), 106, "username", newReservation);
		} catch (ReservationNotFoundException e) {
			fail("Test update room in the reservation: failed because old reservation not found.");
		} catch (RoomNotFoundException e) {
			fail("Test update room in the reservation: failed because new room not found.");
		} catch (CustomerNotFoundException e) {
			fail("Test update room in the reservation: failed because new customer not found.");
		} catch (ReservationAlreadyPresentException e2) {
			fail("Test update room in the reservation: failed because new reservation already present.");
		} catch (RoomAlreadyBookedException e) {
			fail("Test update room in the reservation: failed because new room already booked.");
		} catch (DatabaseManagerException e) {
			fail("Test update room in the reservation: failed.");
		}
		
		readReservation = null;
		try {
			readReservation = manager.readReservation(hotel.getId(), 106, checkInDate1);
		} catch (ReservationNotFoundException e) {
			fail("Test read updated reservation: failed because reservation not found.");
		} catch (DatabaseManagerException e) {
			fail("Test read updated reservation: failed.");
		}

		calendar.set(2020, 11 - 1, 8);
		Date checkInDate2 = calendar.getTime();
		calendar.set(2020, 11 - 1, 14);
		Date checkOutDate2 = calendar.getTime();

		// update reservation changing checkInDate and checkOutDate
		newReservation = new Reservation(checkInDate2, checkOutDate2);
		try {
			manager.updateReservation(hotel.getId(), 106, checkInDate1, hotel.getId(), 106, "username", newReservation);
		} catch (ReservationNotFoundException e) {
			fail("Test update room in the reservation: failed because old reservation not found.");
		} catch (RoomNotFoundException e) {
			fail("Test update room in the reservation: failed because new room not found.");
		} catch (CustomerNotFoundException e) {
			fail("Test update room in the reservation: failed because new customer not found.");
		} catch (ReservationAlreadyPresentException e2) {
			fail("Test update room in the reservation: failed because new reservation already present.");
		} catch (RoomAlreadyBookedException e) {
			fail("Test update room in the reservation: failed because new room already booked.");
		} catch (DatabaseManagerException e) {
			fail("Test update room in the reservation: failed.");
		}
		
		readReservation = null;
		try {
			readReservation = manager.readReservation(hotel.getId(), 106, checkInDate2);
		} catch (ReservationNotFoundException e) {
			fail("Test read updated reservation: failed because reservation not found.");
		} catch (DatabaseManagerException e) {
			fail("Test read updated reservation: failed.");
		}

		Customer customer2 = new Customer("newUsername", "password", "name", "surname");
		try {
			manager.insertCustomer(customer2);
		} catch (DatabaseManagerException | CustomerUsernameAlreadyPresentException e) {
			fail("Read customer: failed.");
		}

		// update reservation changing customer
		newReservation = new Reservation(checkInDate2, checkOutDate2);
		try {
			manager.updateReservation(hotel.getId(), 106, checkInDate2, hotel.getId(), 106, "newUsername",
					newReservation);
		} catch (ReservationNotFoundException e) {
			fail("Test update room in the reservation: failed because old reservation not found.");
		} catch (RoomNotFoundException e) {
			fail("Test update room in the reservation: failed because new room not found.");
		} catch (CustomerNotFoundException e) {
			fail("Test update room in the reservation: failed because new customer not found.");
		} catch (ReservationAlreadyPresentException e2) {
			fail("Test update room in the reservation: failed because new reservation already present.");
		} catch (RoomAlreadyBookedException e2) {
			fail("Test update room in the reservation: failed because new room already booked.");
		} catch (DatabaseManagerException e2) {
			fail("Test update room in the reservation: failed.");
		}
		
		readReservation = null;
		try {
			readReservation = manager.readReservation(hotel.getId(), 106, checkInDate2);
		} catch (ReservationNotFoundException e) {
			fail("Test read updated reservation: failed because reservation not found.");
		} catch (DatabaseManagerException e) {
			fail("Test read updated reservation: failed.");
		}

		// get all reservations of the old customer
		try {
			upcomingReservations = manager.retrieveUpcomingCustomerReservations(customer1);
		} catch (DatabaseManagerException e) {
			fail("Test get reservations of the new customer: failed.");
		}
		assertFalse("Test: reservation deleted for the old customer.", upcomingReservations.contains(readReservation));

		// delete the reservation
		try {
			manager.removeReservation(readReservation);
		} catch (DatabaseManagerException e) {
			fail("Test delete reservation: failed.");
		}

		// get all reservations of the customer
		try {
			upcomingReservations = manager.retrieveUpcomingCustomerReservations(customer2);
		} catch (DatabaseManagerException e) {
			fail("Test get reservations of the new customer: failed.");
		}
		// verify if the reservation was correctly deleted
		assertFalse("Test: reservation deleted.", upcomingReservations.contains(newReservation));

		// add again the reservation
		try {
			manager.insertReservation(hotel.getId(), 106, "newUsername",
					new Reservation(null, checkInDate2, checkOutDate2, null));
		} catch (RoomNotFoundException e) {
			fail("Test add new reservation: failed.");
		} catch (RoomAlreadyBookedException e) {
			fail("Test add new reservation: failed.");
		} catch (CustomerNotFoundException e) {
			fail("Test add new reservation: failed.");
		} catch (DatabaseManagerException e1) {
			fail("Test add new reservation: failed.");
		} catch (ReservationAlreadyPresentException e1) {
			fail("Test add new reservation: failed because reservation already present.");
		}

		// delete room of the reservation
		try {
			manager.deleteRoom(room2);
		} catch (DatabaseManagerException e) {
			fail("Delete room: failed.");
		}

		// try to read the reservation (must be deleted together with the room)
		readReservation = null;
		boolean exception = false;
		try {
			readReservation = manager.readReservation(hotel.getId(), 106, checkInDate2);
		} catch (ReservationNotFoundException e) {
			exception = true;
		} catch (DatabaseManagerException e) {
			fail("Test read deleted reservation: failed.");
		}
		assertTrue("Test read deleted reservation.", exception);

		// add again the reservation
		try {
			manager.insertReservation(hotel.getId(), 105, "newUsername",
					new Reservation(null, checkInDate2, checkOutDate2, null));
		} catch (RoomNotFoundException e) {
			fail("Test add new reservation: failed.");
		} catch (RoomAlreadyBookedException e) {
			fail("Test add new reservation: failed.");
		} catch (CustomerNotFoundException e) {
			fail("Test add new reservation: failed.");
		} catch (DatabaseManagerException e1) {
			fail("Test add new reservation: failed.");
		} catch (ReservationAlreadyPresentException e1) {
			fail("Test add new reservation: failed because reservation already present.");
			;
		}

		// delete customer of the reservation
		try {
			manager.deleteCustomer(customer2);
		} catch (DatabaseManagerException e) {
			fail("Delete customer: failed.");
		}

		// try to read the reservation (must be deleted together with the room)
		readReservation = null;
		exception = false;
		try {
			readReservation = manager.readReservation(hotel.getId(), 105, checkInDate2);
		} catch (ReservationNotFoundException e) {
			exception = true;
		} catch (DatabaseManagerException e) {
			fail("Test read deleted reservation: failed.");
		}
		assertTrue("Test read deleted reservation.", exception);

		try {
			manager.deleteCustomer(customer1);
		} catch (DatabaseManagerException e) {
			fail("Delete customer: failed.");
		}

		try {
			manager.deleteRoom(room1);
		} catch (DatabaseManagerException e) {
			fail("Delete room: failed.");
		}
	}

	// Test for get available/unavailable rooms and set available/unavailable room
	@Test
	public void testGetReservableAndUnreservableSetAvailableAndUnavailableRoom() {
		Hotel hotel = null;
		try {
			hotel = manager.readHotel("Via Bologna 28, Bologna");
		} catch (HotelNotFoundException | DatabaseManagerException e) {
			fail("Read hotel: failed.");
		}

		// room in the database booked in the period from 15-11-2019 (= checkInDate) to
		// 19-11-2019 (= checkOutDate)
		Room bookedRoom = null;
		try {
			bookedRoom = manager.retrieveRoom(hotel.getId(), 401);
		} catch (DatabaseManagerException | RoomNotFoundException e) {
			fail("Read room: failed.");
		}

		// unavailable room in the database
		Room room = null;
		try {
			room = manager.retrieveRoom(hotel.getId(), 302);
		} catch (DatabaseManagerException | RoomNotFoundException e) {
			fail("Read room: failed.");
		}

		Calendar calendar = Calendar.getInstance();

		// test with both startPeriod and endPeriod in the interval [checkInDate,
		// checkOutDate]
		// - bookedRoom and room must not be in the list of reservable rooms for the
		// period
		// - bookedRoom and room must be in the list of unreservable rooms for the
		// period
		calendar.set(2019, 11 - 1, 16, 1, 0, 0);
		Date startPeriod = calendar.getTime();
		calendar.set(2019, 11 - 1, 17, 1, 0, 0);
		Date endPeriod = calendar.getTime();
		List<Room> reservableRooms = null;
		try {
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read reservable rooms: failed");
		}
		assertFalse("Test booked room not in reservable rooms.", reservableRooms.contains(bookedRoom));
		assertFalse("Test unavailable room not in reservable rooms.", reservableRooms.contains(room));
		List<Room> unreservableRooms = null;
		try {
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read unreservable rooms: failed.");
		}
		assertTrue("Test booked room in unreservable rooms.", unreservableRooms.contains(bookedRoom));
		assertTrue("Test unavailable room in unreservable rooms.", unreservableRooms.contains(room));

		// test with checkInDate < startPeriod < checkOutDate
		// - bookedRoom and room must not be in the list of reservable rooms for the
		// period
		// - bookedRoom and room must be in the list of unreservable rooms for the
		// period
		calendar.set(2019, 11 - 1, 18, 1, 0, 0);
		startPeriod = calendar.getTime();
		calendar.set(2019, 11 - 1, 24, 1, 0, 0);
		endPeriod = calendar.getTime();
		try {
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read reservable rooms: failed");
		}
		assertFalse("Test booked room not in reservable rooms.", reservableRooms.contains(bookedRoom));
		assertFalse("Test unavailable room not in reservable rooms.", reservableRooms.contains(room));
		try {
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read unreservable rooms: failed.");
		}
		assertTrue("Test booked room in unreservable rooms.", unreservableRooms.contains(bookedRoom));
		assertTrue("Test unavailable room in unreservable rooms.", unreservableRooms.contains(room));

		// test with checkInDate < endPeriod < checkOutDate
		// - bookedRoom and room must not be in the list of reservable rooms for the
		// period
		// - bookedRoom and room must be in the list of unreservable rooms for the
		// period
		calendar.set(2019, 11 - 1, 11, 1, 0, 0);
		startPeriod = calendar.getTime();
		calendar.set(2019, 11 - 1, 16, 1, 0, 0);
		endPeriod = calendar.getTime();
		try {
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read reservable rooms: failed");
		}
		assertFalse("Test booked room not in reservable rooms.", reservableRooms.contains(bookedRoom));
		assertFalse("Test unavailable room not in reservable rooms.", reservableRooms.contains(room));
		try {
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read unreservable rooms: failed.");
		}
		assertTrue("Test booked room in unreservable rooms.", unreservableRooms.contains(bookedRoom));
		assertTrue("Test unavailable room in unreservable rooms.", unreservableRooms.contains(room));

		// test with both startPeriod and endPeriod outside the interval [checkInDate,
		// checkOutDate]
		// - bookedRoom and room must not be in the list of reservable rooms for the
		// period
		// - bookedRoom and room must be in the list of unreservable rooms for the
		// period
		calendar.set(2019, 11 - 1, 14, 1, 0, 0);
		startPeriod = calendar.getTime();
		calendar.set(2019, 11 - 1, 20, 1, 0, 0);
		endPeriod = calendar.getTime();
		try {
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read reservable rooms: failed");
		}
		assertFalse("Test booked room not in reservable rooms.", reservableRooms.contains(bookedRoom));
		assertFalse("Test unavailable room not in reservable rooms.", reservableRooms.contains(room));
		try {
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			e.printStackTrace();
		}
		assertTrue("Test booked room in unreservable rooms.", unreservableRooms.contains(bookedRoom));
		assertTrue("Test unavailable room in unreservable rooms.", unreservableRooms.contains(room));

		// test with startPeriod < checkInDate and endPeriod < checkInDate
		// - bookedRoom must be in the list of reservable rooms for the period
		// - room must not be in the list of reservable rooms for the period
		// - bookedRoom must not be in the list of unreservable rooms for the period
		// - room must be in the list of unreservable rooms for the period
		calendar.set(2019, 11 - 1, 6, 1, 0, 0);
		startPeriod = calendar.getTime();
		calendar.set(2019, 11 - 1, 10, 1, 0, 0);
		endPeriod = calendar.getTime();
		try {
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read reservable rooms: failed");
		}
		assertTrue("Test booked room in reservable rooms.", reservableRooms.contains(bookedRoom));
		assertFalse("Test unavailable room not in reservable rooms.", reservableRooms.contains(room));
		try {
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read unreservable rooms: failed");
		}
		assertFalse("Test booked room not in unreservable rooms.", unreservableRooms.contains(bookedRoom));
		assertTrue("Test unavailable room in unreservable rooms.", unreservableRooms.contains(room));

		// test with startPeriod > checkOutDate and endPeriod > checkOutDate
		// - bookedRoom must be in the list of reservable rooms for the period
		// - room must not be in the list of reservable rooms for the period
		// - bookedRoom must not be in the list of unreservable rooms for the period
		// - room must be in the list of unreservable rooms for the period
		calendar.set(2019, 11 - 1, 21, 1, 0, 0);
		startPeriod = calendar.getTime();
		calendar.set(2019, 11 - 1, 23, 1, 0, 0);
		endPeriod = calendar.getTime();
		try {
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read reservable rooms: failed");
		}
		assertTrue("Test booked room in reservable rooms.", reservableRooms.contains(bookedRoom));
		assertFalse("Test unavailable room not in reservable rooms.", reservableRooms.contains(room));
		try {
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read unreservable rooms: failed");
		}
		assertFalse("Test booked room not in unreservable rooms.", unreservableRooms.contains(bookedRoom));
		assertTrue("Test unavailable room in unreservable rooms.", unreservableRooms.contains(room));

		// test after setting room available
		// - room must be in the list of reservable rooms for the period
		// - room must not be in the list of unreservable rooms for the period
		try {
			room = manager.setRoomAvailable(room);
		} catch (DatabaseManagerException e) {
			fail("Test set room available: failed");
		}
		try {
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read reservable rooms: failed");
		}
		assertTrue("Test available room in reservable rooms.", reservableRooms.contains(room));
		try {
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read unreservable rooms: failed");
		}
		assertFalse("Test available room not in unreservable rooms.", unreservableRooms.contains(room));

		// coming back to the original situation
		try {
			room = manager.setRoomUnavailable(room);
		} catch (DatabaseManagerException e) {
			fail("Test set room unavailable: failed");
		}
		try {
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read reservable rooms: failed");
		}
		assertFalse("Test unavailable room not in reservable rooms.", reservableRooms.contains(room));
		try {
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
		} catch (DatabaseManagerException e) {
			fail("Read unreservable rooms: failed");
		}
		assertTrue("Test unavailable room in unreservable rooms.", unreservableRooms.contains(room));
	}

	// ------------------------------------------------------------------------\\
	// Key Value \\
	// ------------------------------------------------------------------------\\

	@Test
	public void insertReadBooking() {

		String startTest = manager.keyValue.toStringKeyValue();

		Booking firstBooking = new Booking("Alessio", "Ercolani", "44");
		Booking secondBooking = new Booking("Marco", "Del Gamba", "66");
		Booking thirdBooking = new Booking("Chiara", "Bonsignori", "23");

		int idBooking = 500;

		try {
			manager.keyValue.insertBooking(Integer.toString(idBooking++), firstBooking);
			manager.keyValue.insertBooking(Integer.toString(idBooking++), secondBooking);

		} catch (DatabaseManagerException e) {
			fail("Impossible to insert a new booking: failed");
		} catch (BookingAlreadyPresentException b) {
			fail("Impossible duplication of id " + b.getMessage() + ": failed");
		}

		boolean exception = false;

		try {
			manager.keyValue.insertBooking(Integer.toString(500), thirdBooking);
		} catch (DatabaseManagerException e) {
			fail("Impossible to insert a new booking: failed");
		} catch (BookingAlreadyPresentException e) {
			exception = true;
		}

		assertTrue("Test insertReadBooking.", exception);

		Booking readBooking = null;

		try {
			readBooking = manager.keyValue.getBooking(Integer.toString(500));
		} catch (DatabaseManagerException e) {
			fail("Impossible to read a booking: failed");
		} catch (BookingNotFoundException bnf) {
			fail("Impossible: the booking is on the database");
		}

		assertEquals("Test insertReadBooking", firstBooking, readBooking);

		exception = false;

		try {
			readBooking = manager.keyValue.getBooking(Integer.toString(-1));
		} catch (DatabaseManagerException e) {
			fail("Impossible to read a booking: failed");
		} catch (BookingNotFoundException bnf) {
			exception = true;
		}

		assertTrue("Test insertReadBooking.", exception);
		try {
			manager.keyValue.deleteBooking(Integer.toString(500));
			manager.keyValue.deleteBooking(Integer.toString(501));
		} catch (DatabaseManagerException e) {

			fail("Impossible to delete a booking: failed");
		}

		assertEquals("Test insertReadBooking", startTest, manager.keyValue.toStringKeyValue());

		try {
			manager.keyValue.closeKeyValueDb();
		} catch (DatabaseManagerException e) {
			fail("Impossible to close the key-value database");
		}
	}
}
