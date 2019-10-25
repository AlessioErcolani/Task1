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
		} catch (CustomerNotFound e) {
			fail("customer exists, error!");
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
		} catch (HotelNotFound e) {
			fail("hotel exists, error!");
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}
	}
	
/*	@Test
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
			manager.deleteCustomer(readCustomer);
		} catch (CustomerNotFound e) {
			fail("customer exists, error!");
		} catch (DatabaseManagerException e) {
			fail(e.getMessage());
		}		
	}
		
	}
	
	
		
	}*/
@Test
public void testMenageReservation() {		
	java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE); //OFF
	
	try {
		Receptionist receptionist = new Receptionist("r1");
		Customer customer = receptionist.getHotelManager().readCustomer("piergiorgio");
		Hotel hotel = receptionist.getHotelManager().readHotel("Via Bologna 28, Bologna");
		Room room = receptionist.getHotelManager().readRoom(hotel.getHotelId(), 101);	
			Calendar calendar = Calendar.getInstance();
			calendar.set(2020, 11 - 1, 6, 1, 0, 0);			
			Date checkInDate = calendar.getTime();
			
			calendar.set(2020, 11 - 1, 11, 1, 0, 0);			
			Date checkOutDate = calendar.getTime();
			
			// add a new reservation
			Reservation reservation= receptionist.addReservation(room, customer, checkInDate, checkOutDate);
			reservation = receptionist.getHotelManager().readReservation(hotel.getHotelId(), room.getRoomNumber(), checkInDate);
			
			List<Reservation> upcomingReservations = customer.getUpcomingReservations();	
			boolean result = upcomingReservations.contains(reservation);
			assertTrue("Test inserted reservation", result);
			
			receptionist.deleteReservation(checkInDate, room);
			upcomingReservations = customer.getUpcomingReservations();
			result = upcomingReservations.contains(reservation);
			assertFalse("Test deleted reservation", result);			
		} catch (DatabaseManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
