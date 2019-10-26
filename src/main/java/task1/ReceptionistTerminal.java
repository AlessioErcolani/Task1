package task1;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import exc.CustomerUsernameAlreadyPresentException;
import exc.DatabaseManagerException;

public class ReceptionistTerminal extends Terminal {
	
	private Receptionist receptionist;
	
	private final static List<String> commands = Arrays.asList(
			"show-hotels",
			"show-rooms",
			"show-reservations",
			"delete-reservation",
			"register",
			"help",
			"logout"
			);
	
	private final static Map<String, Options> optionsMap;
	
	static {
		Map<String, Options> map = new HashMap<>();
		
		map.put("show-hotels", new Options());
		map.put("show-rooms", getOptionsForShowRooms());
		map.put("show-reservations", getOptionsForShowReservations());
		map.put("delete-reservation", getOptionsForDeleteReservation());
		map.put("register", getOptionsForRegister());
		map.put("help", new Options());
		map.put("logout", new Options());
		
		optionsMap = map;
	}
	
	//------------------------------------------------------------------------\\
	// @Constructors                                                          \\
	//------------------------------------------------------------------------\\

	public ReceptionistTerminal(Receptionist receptionist, Scanner scanner) {
		super(scanner);
		this.receptionist = receptionist;
	}

	public ReceptionistTerminal(Receptionist receptionist) {
		this(receptionist, new Scanner(System.in));
	}
	
	//------------------------------------------------------------------------\\
	// @Override methods                                                      \\
	//------------------------------------------------------------------------\\
	
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
		case "show-reservations":
			showReservations(options);
			break;
		case "delete-reservation":
			deleteReservation(options);
			break;
		case "register":
			register(options);
			break;
		case "help":
			help(options);
			break;
		case "logout":
			logout();
			break;
		}
	}

	//------------------------------------------------------------------------\\
	// Commands implementation                                                \\
	//------------------------------------------------------------------------\\
	
	private void showHotels() {
		try {
			printHotels(Application.hotelDatabaseManager.getAllHotels());
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
		
	}
	
	private void showRooms(String[] options) {
		try {
        	CommandLine cmd = parser.parse(getOptionsMap().get("show-rooms"), options);
            
        	List<Room> rooms;
            rooms = Application.hotelDatabaseManager.getAvailableRooms(receptionist.getHotel(), new Date());
            
            printRooms(rooms);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("show-rooms", getOptionsMap().get("show-rooms"));
        } catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}
	
	private void showReservations(String[] options) {
		try {
        	CommandLine cmd = parser.parse(getOptionsMap().get("show-reservations"), options);
        	
            List<Reservation> reservations;
            Date date;
        	if (cmd.hasOption("from"))
        		date = parseDate(cmd.getOptionValue("from"));
    		else
    			date = new Date();
            reservations = Application.hotelDatabaseManager.getUpcomingReservations(receptionist.getHotel(), date);
            
			printReservations(reservations);
        } catch (org.apache.commons.cli.ParseException e) {
        	System.out.println(e.getMessage());
            formatter.printHelp("show-reservations", getOptionsMap().get("show-reservations"));
        } catch (java.text.ParseException e) {
        	System.out.println("Date format: yyyy-mm-dd");
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
        	
        	Room room = Application.hotelDatabaseManager.readRoom(hotelId, roomNumber);
        	Application.hotelDatabaseManager.deleteReservation(checkIn, room);
        	
        	System.out.println("Reservation deleted successfully");
        	
        } catch (ParseException e) {
        	System.out.println(e.getMessage());
            formatter.printHelp("delete-reservation", getOptionsMap().get("delete-reservation"));
        } catch (java.text.ParseException e) {
        	System.out.println("Date format: yyyy-mm-dd");
		} catch (Exception e) {
			System.out.println("Unable to delete reservation");
		}
		
	}
	
	private void register(String[] options) {
		try {
        	CommandLine cmd = parser.parse(getOptionsMap().get("register"), options);
        	
        	String name = cmd.getOptionValue("name");
        	String surname = cmd.getOptionValue("surname");
        	String username = cmd.getOptionValue("username");
        	String password = cmd.getOptionValue("password");
        	
        	Customer customer = new Customer(username, password, name, surname);
            
			Application.hotelDatabaseManager.addCustomer(customer);
			System.out.println("Added new customer " + name + " " + surname);
        } catch (ParseException e) {
        	System.out.println(e.getMessage());
            formatter.printHelp("register", getOptionsMap().get("register"));
        } catch (CustomerUsernameAlreadyPresentException e) {
        	System.out.println("Username '" + e.getMessage() + "' already in use");
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
		
	}
	
	private void logout() {
		newUser = true;
		nextUser = null;
	}
	
	//------------------------------------------------------------------------\\
	// Options definition                                                     \\
	//------------------------------------------------------------------------\\

	private static Options getOptionsForShowRooms() {
		Options options = new Options();
        
        return options;
	}
	
	private static Options getOptionsForShowReservations() {
		Options options = new Options();
        
		Option from = new Option("f", "from", true, "the minimun date (yyyy-mm-dd) for the check-in field");
		from.setRequired(false);
		
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
		Option password = new Option("p", "password", true, "customer's password");
		password.setRequired(true);
		
		options.addOption(name);
		options.addOption(surname);
		options.addOption(username);
		options.addOption(password);
		
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
}
