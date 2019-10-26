package task1;

import java.text.SimpleDateFormat;
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

public class ReceptionistTerminal extends Terminal {
	
	private Receptionist receptionist;
	
	private final static List<String> commands = Arrays.asList(
			"show-rooms",
			"show-reservations",
			"help",
			"logout"
			);
	
	private final static Map<String, Options> optionsMap;
	
	static {
		Map<String, Options> map = new HashMap<>();
		
		map.put("show-rooms", getOptionsForShowRooms());
		map.put("show-reservations", getOptionsForShowReservations());
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
		case "show-rooms":
			showRooms(options);
			break;
		case "show-reservations":
			showReservations(options);
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
}
