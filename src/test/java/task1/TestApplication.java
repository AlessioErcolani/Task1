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
		manager =  new HotelManager("hotel_chain");
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
	}
	
	@AfterClass
	public static void finish() {
		manager.exit();
	}
	
	@Test
	public void testAddAndReadCustomer() {
		//test add new customer
		String username = "username";
		String password = "password";
		Customer customer = new Customer(username, password, "name", "surname");
		try {
			manager.addCustomer(customer);
		} catch (CustomerUsernameAlreadyPresentException e) {
			fail("new customer, error!");
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}
		
		//test add customer with an username already present
		Customer customerCopy = new Customer(username, "pwd", "newName","newSurname");
		boolean exceptionCatched = false;
		try {
			manager.addCustomer(customerCopy);
		} catch (CustomerUsernameAlreadyPresentException e) {
			exceptionCatched = true;			
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}
		assertTrue(exceptionCatched);
		
		//test read customer by username and password
		Customer readCustomer = null;
		try {
			readCustomer = manager.authenticateCustomer(username, password);
		} catch (CustomerAuthenticationFailure e) {
			fail("username and password are correct, error!");
		}
		assertEquals(customer, readCustomer);	
		
		//test delete a customer
		try {
			manager.deleteCustomer(readCustomer);
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void addAndReadHotel() {
		String address = "Via Ferrara 45, Ferrara";
		
		//test add new hotel
		Hotel hotel = new Hotel(address);
		try {
			manager.addHotel(hotel);
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}
		
		//test read hotel		
		Hotel readHotel = null;
		try {
			readHotel = manager.readHotel(address);
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}
		assertEquals(readHotel, hotel);	
		
		//test delete an hotel
		try {
			manager.deleteHotel(readHotel);
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAddAndReadReceptionist() {
		//test add new receptionist
		String address = "Via Ferrara 44, Ferrara";
		
		Hotel hotel = new Hotel(address);
		
		try {
			manager.addHotel(hotel);
		} catch (DatabaseManagerException ex) {
			fail(ex.getMessage());
		}
			
		String username = "username";
		String password = "password";
		Receptionist receptionist = new Receptionist(username, password, "name", "surname", hotel);
		try {
			manager.addReceptionist(receptionist);
		} catch (ReceptionistUsernameAlreadyPresentException e) {
			fail("new receptionist, error!");
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}
		
		//test add receptionist with an username already present
		Receptionist ReceptionistCopy = new Receptionist(username, "pwd", "newName","newSurname", hotel);
		boolean exceptionCatched = false;
		try {
			manager.addReceptionist(ReceptionistCopy);
		} catch (ReceptionistUsernameAlreadyPresentException e) {
			exceptionCatched = true;			
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}
		assertTrue(exceptionCatched);
		
		//test read receptionist by username and password
		Receptionist readReceptionist = null;
		try {
			readReceptionist = manager.authenticateReceptionist(username, password);
		} catch (ReceptionistAuthenticationFailure e) {
			fail("username and password are correct, error!");
		}
		assertEquals(receptionist, readReceptionist);	
		
		//test delete a customer		
		try {
		manager.deleteReceptionist(readReceptionist);		
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}	
		
		//test delete an hotel
		// If an hotel is deleted first, all the correlated receptionists are deleted
		try {
			manager.deleteHotel(hotel);
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}
		
	}
	
	// Test for add, update and delete reservation
	@Test
	public void testAddUpdateDeleteReservation() {		
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
			Reservation reservation = manager.addReservation(room, customer, checkInDate, checkOutDate);
			reservation = manager.readReservation(hotel.getHotelId(), room.getRoomNumber(), checkInDate);
			
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
			Reservation newReservation = new Reservation(newRoom, newCheckInDate, newCheckOutDate);
			newReservation.setCustomer(newCustomer);
			manager.updateReservation(reservation, newReservation);	
			
			// delete the reservation
			manager.deleteReservation(newCheckInDate, newRoom);
			
			// get all reservations of the customer
			upcomingReservations = customer.getUpcomingReservations();
			
			// verify if the reservation was correctly deleted
			assertFalse("Test: reservation deleted", upcomingReservations.contains(newReservation));	
		} catch (DatabaseManagerException e) {
			e.printStackTrace();
		}	
	}

	// Test for get available/unavailable rooms and set available/unvailable room
	@Test
	public void testGetAndSetAvailableAndUnavailableRoom() {		
		try {	
			Hotel hotel = manager.readHotel("Via Bologna 28, Bologna");
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(2019, 11 - 1, 15, 1, 0, 0);	
			Date day = calendar.getTime();
			
			// get available rooms in an hotel for a given day
			List<Room> availableRooms = manager.getAvailableRooms(hotel, day);
			
			// set an available room as not available
			Room room = availableRooms.get(0);
			room = manager.setRoomUnavailable(room.getHotel(), room.getRoomNumber());
			
			// get unavailable rooms in the other for the day
			List<Room> unavailableRooms = manager.getUnavailableRooms(hotel, day);
			
			// verify if the room is correctly updated
			assertTrue("Test: set room unavailable", unavailableRooms.contains(room));
			
			// set the unavailable room as available
			room = manager.setRoomAvailable(room.getHotel(), room.getRoomNumber());
			
			// get available rooms for the day
			availableRooms = manager.getAvailableRooms(hotel, day);
			
			// verify if the room is correctly updated
			assertTrue("Test: set room available", availableRooms.contains(room));
		} catch (DatabaseManagerException e) {
			e.printStackTrace();
		}	
	}
}
