package task1;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.junit.Test;

import exc.DatabaseManagerException;

public class TestApplication {

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
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
