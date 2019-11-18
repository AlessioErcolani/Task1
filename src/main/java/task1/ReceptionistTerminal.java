package task1;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import exc.*;

public class ReceptionistTerminal extends Terminal {

	private Receptionist receptionist;

	private final static List<String> commands = Arrays.asList(	"show-hotels", 
																"show-rooms", 
																"add-reservation",
																"show-reservations", 
																"update-reservation", 
																"delete-reservation", 
																"set-room", 
																"register", 
																"check-in",
																"check-out", 
																"sim-key-value", 
																"help", 
																"logout");

	private final static Map<String, Options> optionsMap;

	static {
		Map<String, Options> map = new HashMap<>();

		map.put("show-hotels", new Options());
		map.put("show-rooms", getOptionsForShowRooms());
		map.put("add-reservation", getOptionsForAddReservation());
		map.put("show-reservations", getOptionsForShowReservations());
		map.put("update-reservation", getOptionsForUpdateReservation());
		map.put("delete-reservation", getOptionsForDeleteReservation());
		map.put("set-room", getOptionsForSetRoom());
		map.put("register", getOptionsForRegister());
		map.put("check-in", getOptionsForCheckIn());
		map.put("check-out", getOptionsForCheckOut());
		map.put("sim-key-value", getOptionsForSimulateKeyValue());
		map.put("help", new Options());
		map.put("logout", new Options());

		optionsMap = map;
	}

	// ------------------------------------------------------------------------\\
	// Constructors															   \\
	// ------------------------------------------------------------------------\\

	public ReceptionistTerminal(Receptionist receptionist, Scanner scanner) {
		super(scanner);
		this.receptionist = receptionist;
	}

	public ReceptionistTerminal(Receptionist receptionist) {
		this(receptionist, new Scanner(System.in));
	}

	// ------------------------------------------------------------------------\\
	// @Override methods 													   \\
	// ------------------------------------------------------------------------\\

	@Override
	public String getUsername() {
		return receptionist.getUsername();
	}

	@Override
	protected List<String> getCommands() {
		return commands;
	}

	@Override
	protected Map<String, Options> getOptionsMap() {
		return optionsMap;
	}

	@Override
	protected void execute(String command, String[] options) {
		switch (command) {
		case "show-hotels":
			showHotels();
			break;
		case "show-rooms":
			showRooms(options);
			break;
		case "add-reservation":
			addReservation(options);
			break;
		case "show-reservations":
			showReservations(options);
			break;
		case "update-reservation":
			updateReservation(options);
			break;
		case "delete-reservation":
			deleteReservation(options);
			break;
		case "set-room":
			setRoom(options);
			break;
		case "register":
			register(options);
			break;
		case "check-in":
			checkIn(options);
			break;
		case "check-out":
			checkOut(options);
			break;
		case "sim-key-value":
			simulateKeyValue(options);
			break;
		case "help":
			help(options);
			break;
		case "logout":
			logout();
			break;
		}
	}

	// ------------------------------------------------------------------------\\
	// Commands implementation 												   \\
	// ------------------------------------------------------------------------\\

	private void showHotels() {
		try {
			printHotels(Application.hotelDatabaseManager.retrieveHotels());
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	private void showRooms(String[] options) {
		try {
			CommandLine cmd = parser.parse(getOptionsMap().get("show-rooms"), options);

			long hotelId = cmd.hasOption("hotel") ? ((Number) cmd.getParsedOptionValue("hotel")).longValue()
					: receptionist.getHotel().getId();
			Date from, to;

			if (cmd.hasOption("from") && cmd.hasOption("to")) {
				from = parseDate(cmd.getOptionValue("from"));
				to = parseDate(cmd.getOptionValue("to"));
			} else if (cmd.hasOption("from") && !cmd.hasOption("to")) {
				from = parseDate(cmd.getOptionValue("from"));
				
				to = parseDate(cmd.getOptionValue("from"));
				Calendar calendar = Calendar.getInstance(); 
				calendar.setTime(to); 
				calendar.add(Calendar.DATE, 1);
				to = calendar.getTime();
			} else if (!cmd.hasOption("from") && cmd.hasOption("to")) {
				from = new Date();
				to = parseDate(cmd.getOptionValue("to"));
			} else {
				from = new Date();
				
				to = new Date();
				Calendar calendar = Calendar.getInstance(); 
				calendar.setTime(to); 
				calendar.add(Calendar.DATE, 1);
				to = calendar.getTime();
			}

			if (to.before(from) || to.equals(from))
				throw new ParseException("Check-out date must be greater than check-in date");

			List<Room> rooms = null;

			if (cmd.hasOption("notbookable"))
				rooms = Application.hotelDatabaseManager.retrieveUnreservableRooms(hotelId, from, to);
			else if (cmd.hasOption("all"))
				rooms = Application.hotelDatabaseManager.retrieveHotelRooms(hotelId);
			else
				rooms = Application.hotelDatabaseManager.retrieveReservableRooms(hotelId, from, to);
			
			Hotel hotel = Application.hotelDatabaseManager.retrieveHotel(hotelId);

			if (cmd.hasOption("notbookable"))
				System.out.println("Non-bookable rooms in hotel '" + hotel.getAddress() + "' from " + dateToString(from)
						+ " to " + dateToString(to));
			else if (cmd.hasOption("all"))
				System.out.println("Rooms of hotel '" + hotel.getAddress() + "'");
			else
				System.out.println("Bookable rooms in hotel '" + hotel.getAddress() + "' from " + dateToString(from)
						+ " to " + dateToString(to));
			printRooms(rooms);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("show-rooms", getOptionsMap().get("show-rooms"), true);
		} catch (java.text.ParseException e) {
			System.out.println("Date format: yyyy-mm-dd");
		} catch (HotelNotFoundException e) {
			System.out.println("Hotel " + e.getMessage() + " not found");
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	private void addReservation(String[] options) {
		try {
			CommandLine cmd = parser.parse(getOptionsMap().get("add-reservation"), options);

			long hotelId = ((Number) cmd.getParsedOptionValue("hotel")).longValue();
			int roomNumber = ((Number) cmd.getParsedOptionValue("room")).intValue();
			String username = cmd.getOptionValue("customer");
			Date from = parseDate(cmd.getOptionValue("from"));
			Date to;
			if (cmd.hasOption("to"))
				to = parseDate(cmd.getOptionValue("to"));
			else
				to = parseDate(cmd.getOptionValue("from"));

			if (to.before(from) || to.equals(from))
				throw new ParseException("Check-out date must be greater than check-in date");

			Reservation reservation = new Reservation(from, to);
			reservation = Application.hotelDatabaseManager.insertReservation(hotelId, roomNumber, username, reservation);	
			System.out.println("Reservation added successfully");
			printReservations(Arrays.asList(reservation));
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("add-reservation", getOptionsMap().get("add-reservation"), true);
		} catch (java.text.ParseException e) {
			System.out.println("Date format: yyyy-mm-dd");
		} catch (RoomNotFoundException e) {
			System.out.println("Room not found");
		} catch (CustomerNotFoundException e) {
			System.out.println("Customer '" + e.getMessage() + "' not found");
		} catch (RoomAlreadyBookedException e) {
			System.out.println("The specified room is already booked in that period");
		} catch (ReservationAlreadyPresentException e) {
			System.out.println("Reservation already present");
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	private void showReservations(String[] options) {
		try {
        	CommandLine cmd = parser.parse(getOptionsMap().get("show-reservations"), options);
        	
        	long hotelId = cmd.hasOption("hotel") ?
        			((Number) cmd.getParsedOptionValue("hotel")).longValue() :
        			receptionist.getHotel().getId();
        	
            Date date;
        	if (cmd.hasOption("from"))
        		date = parseDate(cmd.getOptionValue("from"));
    		else
    			date = new Date();
        	
        	Hotel hotel = Application.hotelDatabaseManager.retrieveHotel(hotelId);
        	List<Reservation> reservations = Application.hotelDatabaseManager.retrieveUpcomingReservations(hotelId, date);
            
            System.out.println("Reservations from " + dateToString(date) + " in hotel '" + hotel.getAddress() + "'");
			printReservations(reservations);
		} catch (org.apache.commons.cli.ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("show-reservations", getOptionsMap().get("show-reservations"), true);
		} catch (java.text.ParseException e) {
			System.out.println("Date format: yyyy-mm-dd");
		} catch (HotelNotFoundException e) {
			System.out.println("Hotel not found");
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}
	
	private void updateReservation(String[] options) {
		try {
        	CommandLine cmd = parser.parse(getOptionsMap().get("update-reservation"), options);
        	
        	// get values of mandatory options
        	long oldHotelId = ((Number) cmd.getParsedOptionValue("currenthotel")).longValue();
        	int oldRoomNumber = ((Number) cmd.getParsedOptionValue("currentroom")).intValue();
        	Date oldCheckIn = parseDate(cmd.getOptionValue("currentcheckin"));
        	
        	// check if at least one optional option is present
        	if (	!cmd.hasOption("hotel")
        			&& !cmd.hasOption("room")
        			&& !cmd.hasOption("customer")
        			&& !cmd.hasOption("from")
        			&& !cmd.hasOption("to"))
        		throw new ParseException("Provide at least one new value to update the reservation");
        	
        	// get values of optional parameters
        	long newHotelId = cmd.hasOption("hotel") ? ((Number) cmd.getParsedOptionValue("hotel")).longValue() : oldHotelId;
        	int newRoomNumber = cmd.hasOption("room") ? ((Number) cmd.getParsedOptionValue("room")).intValue() : oldRoomNumber;
        	String newUsername = cmd.hasOption("customer") ? cmd.getOptionValue("customer") : null;
        	Date newCheckIn = cmd.hasOption("from") ? parseDate(cmd.getOptionValue("from")) : oldCheckIn;
        	Date newCheckOut = cmd.hasOption("to") ? parseDate(cmd.getOptionValue("to")) : null;
        		
        	Reservation reservation = Application.hotelDatabaseManager.updateReservation(oldHotelId, oldRoomNumber, oldCheckIn, newHotelId, newRoomNumber, newUsername, newCheckIn, newCheckOut);     
        	System.out.println("Reservation updated successfully");
        	printReservations(Arrays.asList(reservation));      	
        } catch (ParseException e) {
        	System.out.println(e.getMessage());
            formatter.printHelp("update-reservation", getOptionsMap().get("update-reservation"), true);
        } catch (java.text.ParseException e) {
        	System.out.println("Date format: yyyy-mm-dd");
		} catch (ReservationNotFoundException e) {
			System.out.println("Reservation not found");
		} catch (CustomerNotFoundException e) {
			System.out.println("Customer '" + e.getMessage() + "' not found");
		} catch (RoomNotFoundException e) {
			System.out.println("The specified room does not exist");
		} catch (ReservationAlreadyPresentException e) {
			System.out.println("You did not provided any modification to the reservation");
		} catch (RoomAlreadyBookedException e) {
			System.out.println("The specified room is not bookable in that period");
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	private void deleteReservation(String[] options) {
		try {
        	CommandLine cmd = parser.parse(getOptionsMap().get("delete-reservation"), options);
        	
        	long hotelId = ((Number) cmd.getParsedOptionValue("hotel")).longValue();
        	int roomNumber = ((Number) cmd.getParsedOptionValue("room")).intValue();
        	Date checkIn = parseDate(cmd.getOptionValue("date"));
        	
        	Reservation reservation = Application.hotelDatabaseManager.deleteReservation(hotelId, roomNumber, checkIn);
        	System.out.println("Reservation deleted successfully");
        	printReservations(Arrays.asList(reservation));
        } catch (ParseException e) {
        	System.out.println(e.getMessage());
            formatter.printHelp("delete-reservation", getOptionsMap().get("delete-reservation"), true);
        } catch (java.text.ParseException e) {
        	System.out.println("Date format: yyyy-mm-dd");
		} catch (ReservationNotFoundException e) {
			System.out.println("The specified reservation does not exist");
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	private void setRoom(String[] options) {
		try {
        	CommandLine cmd = parser.parse(getOptionsMap().get("set-room"), options);
        	
        	long hotelId = ((Number) cmd.getParsedOptionValue("hotel")).longValue();
        	int roomNumber = ((Number) cmd.getParsedOptionValue("room")).intValue();
        	
        	Room updatedRoom = null;
        	
        	if (cmd.hasOption("available"))
        		updatedRoom = Application.hotelDatabaseManager.updateRoomAvailability(hotelId, roomNumber, true);
        	else if (cmd.hasOption("notavailable"))
        		updatedRoom = Application.hotelDatabaseManager.updateRoomAvailability(hotelId, roomNumber, false);
        	
        	System.out.println("Room updated successfully");
        	printRooms(Arrays.asList(updatedRoom));     	
        } catch (ParseException e) {
        	System.out.println(e.getMessage());
            formatter.printHelp("set-room", getOptionsMap().get("set-room"), true);
        } catch (RoomNotFoundException e) {
			System.out.println("Room not found");
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	private void register(String[] options) {
		try {
        	CommandLine cmd = parser.parse(getOptionsMap().get("register"), options);
        	
        	String name = cmd.getOptionValue("name");
        	String surname = cmd.getOptionValue("surname");
        	String username = cmd.getOptionValue("username");
        	String password = "pwd";
        	
        	Customer customer = new Customer(username, password, name, surname);
            
			Application.hotelDatabaseManager.insertCustomer(customer);
			System.out.println("Added new customer " + name + " " + surname);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("register", getOptionsMap().get("register"), true);
		} catch (CustomerUsernameAlreadyPresentException e) {
			System.out.println("Username '" + e.getMessage() + "' already in use");
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	private void checkIn(String[] options) {
		try {
        	CommandLine cmd = parser.parse(getOptionsMap().get("check-in"), options);
        	
        	long reservationId = ((Number) cmd.getParsedOptionValue("id")).longValue();
        	String id = Long.toString(reservationId);
        	
        	// used to simulate the key value down
        	if(Application.hotelDatabaseManager.keyValue.isAvailable) {
        		// key-value database is up
        		Booking booking = Application.hotelDatabaseManager.keyValue.getBooking(id);
        		System.out.println("Customer: " + booking.getName() + " " + booking.getSurname() + "\nRoom: " + booking.getRoomNumber());
        	} else {
        		// key-value database is down
        		System.out.println("Simulating the key-value down...");
	        	
	        	Reservation reservation = Application.hotelDatabaseManager.retrieveReservation(reservationId);
	        	
	        	Customer customer = reservation.getCustomer();
	        	
	        	System.out.println("Customer: " + customer.getName() + " " + customer.getSurname());
	        	System.out.println("Room: " + reservation.getRoom().getNumber()); 	        	
        	} 
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("check-in", getOptionsMap().get("check-in"), true);
		} catch (BookingNotFoundException e) {
			System.out.println("The specified reservation does not exist");
		} catch (ReservationNotFoundException e) {
			System.out.println("The specified reservation does not exist");
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	private void checkOut(String[] options) {
		try {
        	CommandLine cmd = parser.parse(getOptionsMap().get("check-out"), options);
        	
        	long reservationId = ((Number) cmd.getParsedOptionValue("id")).longValue();
        	String id = Long.toString(reservationId);
        	
        	if (Application.hotelDatabaseManager.keyValue.isAvailable) {
        		try {
        			Booking booking = Application.hotelDatabaseManager.keyValue.getBooking(id);
        			System.out.println("Check-out for customer " + booking.getSurname() + " in room " + booking.getRoomNumber());
        		} catch (BookingNotFoundException e) {
            		System.out.println("Check-out yet executed");
            	}	
        		    		
        		try {
    				Application.hotelDatabaseManager.keyValue.deleteBooking(id);
    			} catch(KeyValueDatabaseManagerException e) {
    				Application.hotelDatabaseManager.writeErrorLog("[ERROR_CHECK-OUT]: " + e.getMessage() + "\n");
    			}
        	} else {
    			System.out.println("Simulating the key-value down...");
    			Application.hotelDatabaseManager.writeErrorLog("[CHECK-OUT]: " + id + "\n");        			
    		}
        } catch (ParseException e) {
        	System.out.println(e.getMessage());
            formatter.printHelp("check-out", getOptionsMap().get("check-out"), true);
        } catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	private void logout() {
		newUser = true;
		nextUser = null;
	}

	// ------------------------------------------------------------------------\\
	// Simulation 															   \\
	// ------------------------------------------------------------------------\\

	private void simulateKeyValue(String[] options) {
		try {
			CommandLine cmd = parser.parse(getOptionsMap().get("sim-key-value"), options);

			boolean enabled = true;

			if (cmd.hasOption("enable"))
				enabled = true;
			else if (cmd.hasOption("disable"))
				enabled = false;

			Application.hotelDatabaseManager.keyValue.isAvailable = enabled;

			System.out.println("The key-value database is now " + (enabled ? "up" : "down"));

		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("sim-key-value", getOptionsMap().get("sim-key-value"), true);
		}
	}

	// ------------------------------------------------------------------------\\
	// Options definition 													   \\
	// ------------------------------------------------------------------------\\

	private static Options getOptionsForShowRooms() {
		Options options = new Options();

		Option hotel = new Option("h", "hotel", true, "hotel identifier");
		hotel.setRequired(false);
		hotel.setType(Number.class);

		Option bookable = new Option("b", "bookable", false, "show rooms that can be booked (default)");
		bookable.setRequired(false);
		Option notBookable = new Option("n", "notbookable", false, "show rooms that cannot be booked");
		notBookable.setRequired(false);
		Option allRooms = new Option("a", "all", false, "show all rooms");
		notBookable.setRequired(false);

		OptionGroup groupBookable = new OptionGroup();
		groupBookable.addOption(bookable);
		groupBookable.addOption(notBookable);
		groupBookable.addOption(allRooms);
		groupBookable.setRequired(false);

		Option from = new Option("f", "from", true, "check-in date (format: yyyy-mm-dd) (default: today)");
		from.setRequired(false);
		Option to = new Option("t", "to", true, "check-out date: if not specified is equal to the day after the check-in date");
		to.setRequired(false);

		options.addOption(hotel);
		options.addOptionGroup(groupBookable);
		options.addOption(from);
		options.addOption(to);

		return options;
	}

	private static Options getOptionsForShowReservations() {
		Options options = new Options();

		Option hotel = new Option("h", "hotel", true, "hotel identifier");
		hotel.setRequired(false);
		hotel.setType(Number.class);

		Option from = new Option("f", "from", true, "the minimun date (yyyy-mm-dd) for the check-in field");
		from.setRequired(false);

		options.addOption(hotel);
		options.addOption(from);

		return options;
	}

	private static Options getOptionsForRegister() {
		Options options = new Options();

		Option name = new Option("n", "name", true, "customer's name");
		name.setRequired(true);
		Option surname = new Option("s", "surname", true, "customer's surname");
		surname.setRequired(true);
		Option username = new Option("u", "username", true, "customer's username");
		username.setRequired(true);

		options.addOption(name);
		options.addOption(surname);
		options.addOption(username);

		return options;
	}

	private static Options getOptionsForDeleteReservation() {
		Options options = new Options();

		Option hotel = new Option("h", "hotel", true, "hotel identifier");
		hotel.setRequired(true);
		hotel.setType(Number.class);
		Option room = new Option("r", "room", true, "room number");
		room.setRequired(true);
		room.setType(Number.class);
		Option date = new Option("d", "date", true, "check-in date");
		date.setRequired(true);

		options.addOption(hotel);
		options.addOption(room);
		options.addOption(date);

		return options;
	}

	private static Options getOptionsForAddReservation() {
		Options options = new Options();

		Option hotel = new Option("h", "hotel", true, "hotel identifier");
		hotel.setRequired(true);
		hotel.setType(Number.class);
		Option room = new Option("r", "room", true, "room number");
		room.setRequired(true);
		room.setType(Number.class);
		Option customer = new Option("c", "customer", true, "customer's username");
		customer.setRequired(true);
		Option from = new Option("f", "from", true, "check-in date");
		from.setRequired(true);
		Option to = new Option("t", "to", true, "check-out date: if not specified is equal to the day after the check-in date");
		to.setRequired(false);

		options.addOption(hotel);
		options.addOption(room);
		options.addOption(customer);
		options.addOption(from);
		options.addOption(to);

		return options;
	}

	private static Options getOptionsForUpdateReservation() {
		Options options = new Options();

		Option oldHotel = new Option(null, "currenthotel", true, "current hotel identifier");
		oldHotel.setRequired(true);
		oldHotel.setType(Number.class);
		Option oldRoom = new Option(null, "currentroom", true, "current room number");
		oldRoom.setRequired(true);
		oldRoom.setType(Number.class);
		Option oldFrom = new Option(null, "currentcheckin", true, "current check-in date");
		oldFrom.setRequired(true);

		options.addOption(oldHotel);
		options.addOption(oldRoom);
		options.addOption(oldFrom);

		Option hotel = new Option("h", "hotel", true, "new hotel identifier");
		hotel.setRequired(false);
		hotel.setType(Number.class);
		Option room = new Option("r", "room", true, "new room number");
		room.setRequired(false);
		room.setType(Number.class);
		Option customer = new Option("c", "customer", true, "new customer's username");
		customer.setRequired(false);
		Option from = new Option("f", "from", true, "new check-in date");
		from.setRequired(false);
		Option to = new Option("t", "to", true, "new check-out date");
		to.setRequired(false);

		options.addOption(hotel);
		options.addOption(room);
		options.addOption(customer);
		options.addOption(from);
		options.addOption(to);

		return options;
	}

	private static Options getOptionsForSetRoom() {
		Options options = new Options();

		Option hotel = new Option("h", "hotel", true, "hotel identifier");
		hotel.setRequired(true);
		hotel.setType(Number.class);
		Option room = new Option("r", "room", true, "room number");
		room.setRequired(true);
		room.setType(Number.class);

		options.addOption(hotel);
		options.addOption(room);

		Option available = new Option("a", "available", false, "set the room as available");
		available.setRequired(false);
		Option notAvailable = new Option("n", "notavailable", false, "set the room as not available");
		notAvailable.setRequired(false);

		OptionGroup group = new OptionGroup();
		group.addOption(available);
		group.addOption(notAvailable);
		group.setRequired(true);

		options.addOptionGroup(group);

		return options;
	}

	private static Options getOptionsForCheckIn() {
		Options options = new Options();

		Option id = new Option("i", "id", true, "reservation identifier");
		id.setRequired(true);
		id.setType(Number.class);

		options.addOption(id);

		return options;
	}

	private static Options getOptionsForCheckOut() {
		return getOptionsForCheckIn();
	}

	private static Options getOptionsForSimulateKeyValue() {
		Options options = new Options();

		Option enable = new Option("e", "enable", false, "simulation: enable key-value database");
		enable.setRequired(false);
		Option disable = new Option("d", "disable", false, "simulation: key-value database is down");
		disable.setRequired(false);

		OptionGroup group = new OptionGroup();
		group.addOption(enable);
		group.addOption(disable);
		group.setRequired(true);

		options.addOptionGroup(group);

		return options;
	}
}
