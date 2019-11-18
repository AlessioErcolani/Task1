package task1;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import exc.CustomerAuthenticationFailure;
import exc.ReceptionistAuthenticationFailure;

public class Terminal {
	protected boolean end;
	protected Scanner input;
	protected boolean newUser;
	protected User nextUser;
	protected CommandLineParser parser;
	protected HelpFormatter formatter;
	
	//------------------------------------------------------------------------\\
	// Just for testing                                                       \\
	//------------------------------------------------------------------------\\
	
	private static int nextCommandIndex = 0;
	public static String[] testCommandLines;
	
	public static String nextCommand() {
		String command = testCommandLines[nextCommandIndex];
		nextCommandIndex++;
		
		System.out.println(command);
		
		return command;
	}
	
	//------------------------------------------------------------------------\\
	// Commands and Options initialization                                    \\
	//------------------------------------------------------------------------\\

	private final static List<String> commands = Arrays.asList(
			"login",
			"help",
			"exit");
	
	private final static Map<String, Options> optionsMap;
	
	static {
		Map<String, Options> map = new HashMap<>();
		
		map.put("login", getOptionsForLogin());
		map.put("help", new Options());
		map.put("exit", new Options());
		
		optionsMap = map;
	}
	
	//------------------------------------------------------------------------\\
	// Constructors                                                           \\
	//------------------------------------------------------------------------\\

	public Terminal(Scanner scanner) {
		input = scanner;
		end = false;
		nextUser = null;
		parser = new DefaultParser();
		formatter = new HelpFormatter();
		
		System.out.println();
		help(null);
	}

	public Terminal() {
		this(new Scanner(System.in));
	}
	
	//------------------------------------------------------------------------\\
	// Final methods                                                          \\
	//------------------------------------------------------------------------\\

	public final boolean notEnd() {
		return !end;
	}
	
	public final boolean hasNewUser() {
		return newUser;
	}
	
	public final Terminal switchTerminal() {
		if (hasNewUser() && nextUser == null)
			return new Terminal();
		if (hasNewUser() && nextUser instanceof Receptionist)
			return new ReceptionistTerminal((Receptionist) nextUser);
		if (hasNewUser() && nextUser instanceof Customer)
			return new CustomerTerminal((Customer) nextUser);
		return this;
	}

	public final String readCommand() {
		return input.nextLine();
	}
	
	//------------------------------------------------------------------------\\
	// Execution starting point                                               \\
	//------------------------------------------------------------------------\\
	
	public void executeCommandLine(String input) {
		String[] splitCommandLine = input.split(" ");

		// first word is the command name
		String command = splitCommandLine[0];

		// other words are the options
		String[] options = Arrays.copyOfRange(splitCommandLine, 1, splitCommandLine.length);

		if (!getCommands().contains(command))
			System.out.println("Unknown command " + command + "\nType 'help' for a list of commands");
		else
			execute(command, options);
	}
	
	//------------------------------------------------------------------------\\
	// Methods overloaded in subclasses                                       \\
	//------------------------------------------------------------------------\\

	// overloaded in subclasses
	public String getUsername() {
		return "";
	}

	// overloaded in subclasses
	protected List<String> getCommands() {
		return commands;
	}
	
	// overloaded in subclasses
	protected Map<String, Options> getOptionsMap() {
		return optionsMap;
	}

	// overloaded in subclasses
	protected void execute(String command, String[] options) {
		switch (command) {
		case "login":
			login(options);
			break;
		case "help":
			help(options);
			break;
		case "exit":
			exit();
			break;
		}
	}

	//------------------------------------------------------------------------\\
	// Commands implementation                                                \\
	//------------------------------------------------------------------------\\

	private void login(String[] options) {
		try {
        	CommandLine cmd = parser.parse(getOptionsMap().get("login"), options);
            String username = cmd.getOptionValue("username");
            String password = cmd.getOptionValue("password");
            
            if (cmd.hasOption("customer"))
            	nextUser = Application.hotelDatabaseManager.authenticateCustomer(username, password);
            else if (cmd.hasOption("receptionist"))
            	nextUser = Application.hotelDatabaseManager.authenticateReceptionist(username, password);
            newUser = true;
            
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("login", getOptionsMap().get("login"), true);
        } catch (ReceptionistAuthenticationFailure e) {
        	System.out.println("Authentication failed for receptionist " + e.getMessage());
		} catch (CustomerAuthenticationFailure e) {
        	System.out.println("Authentication failed for customer " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Something went wrong");
		}
	}
	
	private void exit() {
		end = true;
	}

	public void help(String[] arguments) {
		// in help case options are interpreted as command names
		if (arguments == null || arguments.length == 0) {
			System.out.println("List of commands:");
			for (String cmd : getCommands())
				System.out.println("\t" + cmd);
			System.out.println("Type 'help <command>' to know its syntax");
		} else {
			for (String cmd : arguments)
				if (getCommands().contains(cmd))
					formatter.printHelp(cmd, getOptionsMap().get(cmd), true);
				else
					System.out.println("Unknown command " + cmd);
		}
		
	}
	
	//------------------------------------------------------------------------\\
	// Print methods                                                          \\
	//------------------------------------------------------------------------\\
	
	protected void printHotels(List<Hotel> hotels) {
		if (hotels == null)
			return;

		String format = "| %-4d | %-25s |%n";

		System.out.format("+------+---------------------------+%n");
		System.out.format("| Id   | Hotel Address             |%n");
		System.out.format("+------+---------------------------+%n");
		for (Hotel h : hotels)
			System.out.format(format,
					h.getId(),
					h.getAddress());
		System.out.format("+------+---------------------------+%n");
	}
	
	protected void printRooms(List<Room> rooms) {
		if (rooms == null)
			return;

		String format = "| %-4d | %-8d | %-26s | %-9s |%n";

		System.out.format("+------+----------+----------------------------+-----------+%n");
		System.out.format("| Room | Capacity | Hotel                      | Available |%n");
		System.out.format("+------+----------+----------------------------+-----------+%n");
		for (Room r : rooms)
			System.out.format(format,
					r.getNumber(),
					r.getCapacity(),
					r.getHotel().getId() + ") " + r.getHotel().getAddress(),
					r.isAvailable() ? "yes" : "no");
		System.out.format("+------+----------+----------------------------+-----------+%n");
	}

	protected void printReservations(List<Reservation> reservations) {
		if (reservations == null)
			return;

		String format = "| %-4d | %-4d | %-8d | %-26s | %-10s | %-10s | %-17s |%n";

		System.out.format("+------+------+----------+----------------------------+------------+------------+-------------------+%n");
		System.out.format("| Id   | Room | Capacity | Hotel                      | Check-In   | Check-Out  | Customer          |%n");
		System.out.format("+------+------+----------+----------------------------+------------+------------+-------------------+%n");
		for (Reservation r : reservations)
			System.out.format(format,
					r.getId(),
					r.getRoom().getNumber(),
					r.getRoom().getCapacity(),
					r.getRoom().getHotel().getId() + ") " + r.getRoom().getHotel().getAddress(),
					dateToString(r.getCheckInDate()),
					dateToString(r.getCheckOutDate()),
					r.getCustomer().getName() + " " + r.getCustomer().getSurname());
		System.out.format("+------+------+----------+----------------------------+------------+------------+-------------------+%n");
	}
	
	//------------------------------------------------------------------------\\
	// Utilities                                                              \\
	//------------------------------------------------------------------------\\
	
	public String dateToString(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.format(date);
	}
	
	public Date parseDate(String string) throws java.text.ParseException {
		try {
			String[] split = string.split("-");
			
			if (split.length != 3)
				throw new Exception();

			int year = Integer.parseInt(split[0]);
			int month = Integer.parseInt(split[1]);
			int day = Integer.parseInt(split[2]);
			
			if (year < 1000 || year > 3000)
				throw new Exception();
			if (month < 1 || month > 12)
				throw new Exception();
			if (day < 1 || day > 31)
				throw new Exception();
			
			if (month == 2)
				if (Year.isLeap(year) && day > 29)
					throw new Exception();
				else if (day > 28) 
					throw new Exception();
			if ((month == 11 || month == 4 || month == 6 || month == 9) && day > 30)
				throw new Exception();
			if (day > 31) 
				throw new Exception();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.parse(string);
		} catch (Exception e) {
			throw new java.text.ParseException("unable to parse", 0);
		}
	}
	
	//------------------------------------------------------------------------\\
	// Options definition                                                     \\
	//------------------------------------------------------------------------\\
	
	private static Options getOptionsForLogin() {
		Options options = new Options();
		
		Option usernameOption = new Option("u", "username", true, "username");
		usernameOption.setRequired(true);
		
		Option passwordOption = new Option("p", "password", true, "password");
		passwordOption.setRequired(true);

		options.addOption(usernameOption);
        options.addOption(passwordOption);
        
        Option recepionist = new Option("r", "receptionist", false, "login as a receptionist");
        Option customer = new Option("c", "customer", false, "login as a customer");
        
        OptionGroup group = new OptionGroup();
        group.addOption(recepionist);
        group.addOption(customer);
        group.setRequired(true);
        options.addOptionGroup(group);
        
        return options;
	}
}
