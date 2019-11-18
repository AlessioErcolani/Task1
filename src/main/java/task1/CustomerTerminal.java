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
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import exc.HotelNotFoundException;

public class CustomerTerminal extends Terminal {

	private Customer customer;

	private final static List<String> commands = Arrays.asList(	"show-reservations", 
																"show-hotels", 
																"show-rooms",
																"change-password", 
																"help", 
																"logout");

	private final static Map<String, Options> optionsMap;

	static {
		Map<String, Options> map = new HashMap<>();

		map.put("show-reservations", getOptionsForShowReservations());
		map.put("show-hotels", new Options());
		map.put("show-rooms", getOptionsForShowRooms());
		map.put("change-password", getOptionsForChangePassword());
		map.put("help", new Options());
		map.put("logout", new Options());

		optionsMap = map;
	}

	// ------------------------------------------------------------------------\\
	// Constructors 														   \\
	// ------------------------------------------------------------------------\\

	public CustomerTerminal(Customer customer, Scanner scanner) {
		super(scanner);
		this.customer = customer;
	}

	public CustomerTerminal(Customer customer) {
		this(customer, new Scanner(System.in));
	}

	// ------------------------------------------------------------------------\\
	// @Override methods 										       		   \\
	// ------------------------------------------------------------------------\\

	@Override
	public String getUsername() {
		return customer.getUsername();
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
		case "show-reservations":
			showReservations(options);
			break;
		case "show-hotels":
			showHotels(options);
			break;
		case "show-rooms":
			showRooms(options);
			break;
		case "change-password":
			changePassword(options);
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
	// Commands implementation 								    			   \\
	// ------------------------------------------------------------------------\\

	private void showReservations(String[] options) {
		try {
			parser.parse(getOptionsMap().get("show-reservations"), options);

			System.out.println("Your upcoming reservations:");
			printReservations(Application.hotelDatabaseManager.retrieveUpcomingCustomerReservations(customer));
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("show-reservations", getOptionsMap().get("show-reservations"), true);
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	private void showHotels(String[] options) {
		try {
			parser.parse(getOptionsMap().get("show-hotels"), options);
		
			printHotels(Application.hotelDatabaseManager.retrieveHotels());
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("show-hotels", getOptionsMap().get("show-hotels"), true);
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	private void showRooms(String[] options) {
		try {
			CommandLine cmd = parser.parse(getOptionsMap().get("show-rooms"), options);

			long hotelId = ((Number) cmd.getParsedOptionValue("hotel")).longValue();
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

			List<Room> rooms = Application.hotelDatabaseManager.retrieveReservableRooms(hotelId, from, to);

			Hotel hotel = Application.hotelDatabaseManager.retrieveHotel(hotelId);

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

	private void changePassword(String[] options) {
		try {
			CommandLine cmd = parser.parse(getOptionsMap().get("change-password"), options);

			String newPassword = cmd.getOptionValue("newpassword");
			customer = Application.hotelDatabaseManager.updatePassword(customer, newPassword);
			System.out.println("Password changed successfully");
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("change-password", getOptionsMap().get("change-password"), true);
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}

	private void logout() {
		newUser = true;
		nextUser = null;
	}

	// ------------------------------------------------------------------------\\
	// Options definition 													   \\
	// ------------------------------------------------------------------------\\

	private static Options getOptionsForShowReservations() {
		Options options = new Options();

		return options;
	}

	private static Options getOptionsForShowRooms() {
		Options options = new Options();

		Option hotel = new Option("h", "hotel", true, "hotel identifier");
		hotel.setRequired(true);
		hotel.setType(Number.class);

		Option from = new Option("f", "from", true, "check-in date (format: yyyy-mm-dd) (default: today)");
		from.setRequired(false);
		Option to = new Option("t", "to", true, "check-out date: if not specified is equal to the day after the check-in date");
		to.setRequired(false);

		options.addOption(hotel);
		options.addOption(from);
		options.addOption(to);

		return options;
	}

	private static Options getOptionsForChangePassword() {
		Options options = new Options();

		Option newPassword = new Option("n", "newpassword", true, "new password");
		newPassword.setRequired(true);

		options.addOption(newPassword);

		return options;
	}
}
