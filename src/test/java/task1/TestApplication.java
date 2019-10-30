package task1;

import static org.junit.Assert.*;
import java.util.*;
import java.util.logging.Level;
import org.junit.*;
import exc.*;

public class TestApplication {

	private static HotelManager manager;
	
	@BeforeClass
	public static void setup() {
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
		manager =  new HotelManager("hotel_chain");
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
			manager.addCustomer(customer);
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
		Customer customerCopy = new Customer("username", "newPwd", "newName","newSurname");
		boolean exception = false;
		try {
			manager.addCustomer(customerCopy);
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
		} catch (DatabaseManagerException e1) {
			fail("Test add room: failed.");
		}
		
		// read room
		Room readRoom = null;
		try {
			readRoom = manager.readRoom(hotel.getHotelId(), 101);
		} catch (RoomNotFoundException e1) {
			fail("Test read room: failed.");
		} catch (DatabaseManagerException e1) {
			fail("Test read room: failed.");
		} 
		assertTrue("Test read room.", room.equals(readRoom));
		
		// try to add again the room
		exception = false;
		try {
			manager.addRoom(room);
		} catch (RoomAlreadyPresentException e) {
			exception = true;
		} catch (DatabaseManagerException e1) {
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
		try {
			manager.addRoom(room);
		} catch (RoomAlreadyPresentException e) {
			fail("Test add room: failed because room already present.");
		} catch (DatabaseManagerException e1) {
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
			readRoom = manager.readRoom(hotel.getHotelId(), 101);
		} catch (RoomNotFoundException e1) {
			exception = true;
		} catch (DatabaseManagerException e1) {
			fail("Test read unexisting room room: failed.");
		} 
		assertTrue("Test read room.", exception);	
	}
	
	// arrivata qui!!
	@Test
	public void testAddAndReadReceptionist() {	
		//test add new receptionist
		String address = "Via Ferrara 44, Ferrara";
		
		Hotel hotel = new Hotel(address);
		
		/*try {
			manager.addHotel(hotel);
		} catch (DatabaseManagerException ex) {
			fail(ex.getMessage());
		}*/
		
		Receptionist receptionist = new Receptionist("username", "pwd", "name", "surname", hotel);
		try {
			manager.addReceptionist(receptionist);
		} catch (ReceptionistUsernameAlreadyPresentException e) {
			fail("Test add new receptionist: failed because username already present!");
		} catch (DatabaseManagerException e) {
			fail("Test add new receptionist: failed.");
		}
		
		// test add receptionist with an username already present
		Receptionist ReceptionistCopy = new Receptionist("username", "newPwd", "newName","newSurname", hotel);
		boolean exceptionCatched = false;
		try {
			manager.addReceptionist(ReceptionistCopy);
		} catch (ReceptionistUsernameAlreadyPresentException e) {
			exceptionCatched = true;			
		} catch (DatabaseManagerException e) {
			fail("Test add new receptionist: failed. ");
		}
		assertTrue("Test add existing receptionist.", exceptionCatched);
		
		// test read receptionist by username and password
		Receptionist readReceptionist = null;
		try {
			readReceptionist = manager.authenticateReceptionist("username", "pwd");
		} catch (ReceptionistAuthenticationFailure e) {
			fail("Test authenticate receptionist: failed.");
		}
		assertEquals("Test authenticate receptionist", receptionist, readReceptionist);		
		
		// test delete a receptionist
		exceptionCatched = false;
		try {
			manager.deleteReceptionist(readReceptionist);
		} catch (DatabaseManagerException e) {
			exceptionCatched = true;
		}		
		assertFalse("Test delete receptionist", exceptionCatched);

		//test delete an hotel
		// If an hotel is deleted first, all the correlated receptionists are deleted
		try {
			manager.deleteHotel(hotel);
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}
		
		// test delete an hotel
		exceptionCatched = false;
		try {
			manager.deleteHotel(hotel);
		} catch (DatabaseManagerException e) {
			exceptionCatched = true;
		}
		assertFalse("Test delete hotel", exceptionCatched);
	}

	
	// Test for add, update and delete reservation
	@Test
	public void testAddUpdateAndDeleteReservation() {		
		try {
			Customer customer = manager.readCustomer("piergiorgio");
			Hotel hotel = manager.readHotel("Via Bologna 28, Bologna");
			Room room = manager.readRoom(hotel.getHotelId(), 101);	
		
			Calendar calendar = Calendar.getInstance();
			calendar.set(2020, 11 - 1, 6, 1, 0, 0);			
			Date checkInDate = calendar.getTime();
			
			calendar.set(2020, 11 - 1, 11, 1, 0, 0);			
			Date checkOutDate = calendar.getTime();
			
			// add a new reservation for a customer
			//Reservation reservation = manager.addReservation(room, customer, checkInDate, checkOutDate);
			manager.addReservation(new Reservation(room, checkInDate, checkOutDate, customer));
			Reservation reservation = manager.readReservation(hotel.getHotelId(), room.getRoomNumber(), checkInDate);
			
			// get all reservations of a customer
			List<Reservation> upcomingReservations = customer.getUpcomingReservations();	
			
			// verify if the new reservation was correctly inserted
			assertTrue("Test: reservation inserted", upcomingReservations.contains(reservation));
			
			// update the reservation
			calendar.set(2020, 11 - 1, 8);
			Date newCheckInDate = calendar.getTime();
			calendar.set(2020, 11 - 1, 14);
			Date newCheckOutDate = calendar.getTime();
			Room newRoom = manager.readRoom(hotel.getHotelId(), 301);
			Customer newCustomer = manager.readCustomer("sara");	
			Reservation newReservation = new Reservation(newRoom, newCheckInDate, newCheckOutDate, newCustomer);
			manager.updateReservation(reservation, newReservation);	
			
			Customer newnewCustomer = manager.readCustomer("marco");
			Reservation newnewReservation = new Reservation(newRoom, newCheckInDate, newCheckOutDate, newnewCustomer);
			manager.updateReservation(reservation, newnewReservation);	
			
			// delete the reservation
			manager.deleteReservation(newCheckInDate, newRoom);
			
			// get all reservations of the customer
			upcomingReservations = customer.getUpcomingReservations();
			
			// verify if the reservation was correctly deleted
			assertFalse("Test: reservation deleted", upcomingReservations.contains(newReservation));	
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	// Test for get available/unavailable rooms and set available/unavailable room
	@Test
	public void testGetReservableAndUnreservableSetAvailableAndUnavailableRoom() {		
		try {				
			Hotel hotel = manager.readHotel("Via Bologna 28, Bologna");		
			
			// room in the database booked in the period from 15-11-2019 (= checkInDate) to 19-11-2019 (= checkOutDate)
			Room bookedRoom = manager.readRoom(hotel.getHotelId(), 401);
			
			// unavailable room in the database
			Room room = manager.readRoom(hotel.getHotelId(), 302);
			
			Calendar calendar = Calendar.getInstance();
			
			// test with both startPeriod and endPeriod in the interval [checkInDate, checkOutDate]
			//  - bookedRoom and room must not be in the list of reservable rooms for the period		
			// 	- bookedRoom and room must be in the list of unreservable rooms for the period				
			calendar.set(2019, 11 - 1, 16, 1, 0, 0);	
			Date startPeriod = calendar.getTime();		
			calendar.set(2019, 11 - 1, 17, 1, 0, 0);	
			Date endPeriod = calendar.getTime();		
			List<Room> reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
			assertFalse(reservableRooms.contains(bookedRoom));
			assertFalse(reservableRooms.contains(room));
			List<Room> unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
			assertTrue(unreservableRooms.contains(bookedRoom));
			assertTrue(unreservableRooms.contains(room));
			
			// test with  checkInDate < startPeriod < checkOutDate  
			//  - bookedRoom and room must not be in the list of reservable rooms for the period		
			// 	- bookedRoom and room must be in the list of unreservable rooms for the period		
			calendar.set(2019, 11 - 1, 18, 1, 0, 0);	
			startPeriod = calendar.getTime();		
			calendar.set(2019, 11 - 1, 24, 1, 0, 0);	
			endPeriod = calendar.getTime();		
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
			assertFalse(reservableRooms.contains(bookedRoom));
			assertFalse(reservableRooms.contains(room));	
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
			assertTrue(unreservableRooms.contains(bookedRoom));
			assertTrue(unreservableRooms.contains(room));
			
			// test with  checkInDate < endPeriod < checkOutDate  
			//  - bookedRoom and room must not be in the list of reservable rooms for the period		
			// 	- bookedRoom and room must be in the list of unreservable rooms for the period		
			calendar.set(2019, 11 - 1, 11, 1, 0, 0);	
			startPeriod = calendar.getTime();		
			calendar.set(2019, 11 - 1, 16, 1, 0, 0);	
			endPeriod = calendar.getTime();		
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
			assertFalse(reservableRooms.contains(bookedRoom));
			assertFalse(reservableRooms.contains(room));
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
			assertTrue(unreservableRooms.contains(bookedRoom));
			assertTrue(unreservableRooms.contains(room));
			
			// test with both startPeriod and endPeriod outside the interval [checkInDate, checkOutDate]
			//  - bookedRoom and room must not be in the list of reservable rooms for the period		
			// 	- bookedRoom and room must be in the list of unreservable rooms for the period		
			calendar.set(2019, 11 - 1, 14, 1, 0, 0);	
			startPeriod = calendar.getTime();		
			calendar.set(2019, 11 - 1, 20, 1, 0, 0);	
			endPeriod = calendar.getTime();		
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
			assertFalse(reservableRooms.contains(bookedRoom));
			assertFalse(reservableRooms.contains(room));	
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
			assertTrue(unreservableRooms.contains(bookedRoom));
			assertTrue(unreservableRooms.contains(room));
			
			// test with startPeriod < checkInDate and endPeriod < checkInDate
			//  - bookedRoom must be in the list of reservable rooms for the period	
			//  - room must not be in the list of reservable rooms for the period	
			// 	- bookedRoom must not be in the list of unreservable rooms for the period	
			//  - room must be in the list of unreservable rooms for the period	
			calendar.set(2019, 11 - 1, 6, 1, 0, 0);	
			startPeriod = calendar.getTime();		
			calendar.set(2019, 11 - 1, 10, 1, 0, 0);	
			endPeriod = calendar.getTime();		
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
			assertTrue(reservableRooms.contains(bookedRoom));	
			assertFalse(reservableRooms.contains(room));
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
			assertFalse(unreservableRooms.contains(bookedRoom));	
			assertTrue(unreservableRooms.contains(room));
			
			// test with startPeriod > checkOutDate and endPeriod > checkOutDate
			//  - bookedRoom must be in the list of reservable rooms for the period	
			//  - room must not be in the list of reservable rooms for the period	
			// 	- bookedRoom must not be in the list of unreservable rooms for the period	
			//  - room must be in the list of unreservable rooms for the period	
			calendar.set(2019, 11 - 1, 21, 1, 0, 0);	
			startPeriod = calendar.getTime();		
			calendar.set(2019, 11 - 1, 23, 1, 0, 0);	
			endPeriod = calendar.getTime();		
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
			assertTrue(reservableRooms.contains(bookedRoom));	
			assertFalse(reservableRooms.contains(room));
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
			assertFalse(unreservableRooms.contains(bookedRoom));
			assertTrue(unreservableRooms.contains(room));
			
			// test after setting room available
			//  - room must be in the list of reservable rooms for the period	
			//  - room must not be in the list of unreservable rooms for the period	
			room = manager.setRoomAvailable(room.getHotel(), room.getRoomNumber());
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
			assertTrue(reservableRooms.contains(room));
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
			assertFalse(unreservableRooms.contains(room));
			
			// coming back to the original situation
			room = manager.setRoomUnavailable(room.getHotel(), room.getRoomNumber());
			reservableRooms = manager.getReservableRooms(hotel, startPeriod, endPeriod);
			assertFalse(reservableRooms.contains(room));
			unreservableRooms = manager.getUnreservableRooms(hotel, startPeriod, endPeriod);
			assertTrue(unreservableRooms.contains(room));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
