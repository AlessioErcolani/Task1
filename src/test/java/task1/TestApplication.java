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
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
		manager =  new HotelManager("hotel_chain");
	}
	
	@AfterClass
	public static void finish() {
		manager.exit();
	}
	
	@Test
	public void testAddAndReadCustomer() {
		// test add new customer
		Customer customer = new Customer("username", "pwd", "name", "surname");
		try {
			manager.addCustomer(customer);
		} catch (CustomerUsernameAlreadyPresentException e) {
			fail("Test add new customer: failed because username already present.");
		} catch (DatabaseManagerException e) {
			fail("Test add new customer: failed.");
		}
		
		// test add customer with an username already present
		Customer customerCopy = new Customer("username", "newPwd", "newName","newSurname");
		boolean exceptionCatched = false;
		try {
			manager.addCustomer(customerCopy);
		} catch (CustomerUsernameAlreadyPresentException e) {
			exceptionCatched = true;			
		} catch (DatabaseManagerException e) {
			fail("Test add new customer: failed. ");
		}
		assertTrue("Test add existing customer.", exceptionCatched);
		
		// test read customer by username and password
		Customer readCustomer = null;
		try {
			readCustomer = manager.authenticateCustomer("username", "pwd");
		} catch (CustomerAuthenticationFailure e) {
			fail("Test authenticate customer: failed.");
		}
		assertEquals("Test authenticate customer", customer, readCustomer);	
		
		// test delete a customer
		exceptionCatched = false;
		try {
			manager.deleteCustomer(readCustomer);
		} catch (CustomerNotFound e) {
			exceptionCatched = true;
		} catch (DatabaseManagerException e) {
			exceptionCatched = true;
		}		
		assertFalse("Test delete customer", exceptionCatched);
	}
	
	@Test
	public void testAddAndReadHotel() {
		String address = "Via Ferrara 45, Ferrara";
		
		// test add new hotel
		Hotel hotel = new Hotel(address);
		try {
			manager.addHotel(hotel);
		} catch (DatabaseManagerException e) {
			fail("Test add new hotel: failed.");
		}
		
		// test read hotel		
		Hotel readHotel = null;
		try {
			readHotel = manager.readHotel(address);
		} catch (DatabaseManagerException e) {
			fail("Test read hotel: failed.");
		}
		assertEquals("Test read hotel.", readHotel, hotel);	
		
		// test delete an hotel
		boolean exceptionCatched = false;
		try {
			manager.deleteHotel(readHotel);
		} catch (HotelNotFound e) {
			exceptionCatched = true;
		} catch (DatabaseManagerException e) {
			exceptionCatched = true;
		}
		assertFalse("Test delete hotel", exceptionCatched);
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
		} catch (ReceptionistNotFound e) {
			exceptionCatched = true;
		} catch (DatabaseManagerException e) {
			exceptionCatched = true;
		}		
		assertFalse("Test delete receptionist", exceptionCatched);
		
		// test delete an hotel
		exceptionCatched = false;
		try {
			manager.deleteHotel(hotel);
		} catch (HotelNotFound e) {
			exceptionCatched = true;
		} catch (DatabaseManagerException e) {
			exceptionCatched = true;
		}
		assertFalse("Test delete hotel", exceptionCatched);
	}
	
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
