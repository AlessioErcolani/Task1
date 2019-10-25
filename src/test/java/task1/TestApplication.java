package task1;

import static org.junit.Assert.*;

import java.util.logging.Level;

import org.junit.*;
import org.junit.Test;
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

	
}
