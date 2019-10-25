package task1;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Terminal {
	protected boolean end;
	protected Scanner input;
	protected boolean newUser;
	protected User nextUser;

	private final static List<String> commands = Arrays.asList(
			"login-c",
			"login-r",
			"help",
			"exit");

	public Terminal(Scanner scanner) {
		input = scanner;
		end = false;
		nextUser = null;
	}

	public Terminal() {
		this(new Scanner(System.in));
	}

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
	
	public void executeCommandLine(String input) {
		String[] splitCommandLine = input.split(" ");

		// first word is the command name
		String command = splitCommandLine[0];

		// other words are the options
		String[] options = Arrays.copyOfRange(splitCommandLine, 1, splitCommandLine.length);

		/*
		 * // TODO: execute true commands System.out.println("Command: " + command);
		 * System.out.println("Options:"); for (String o : options)
		 * System.out.println("\t" + o); end = true;
		 */

		if (!getCommands().contains(command))
			System.out.println("Unknown command " + command + "\nType 'help' for a list of commands");
		else
			execute(command, options);
	}

	// overloaded in subclasses
	public String getUsername() {
		return "";
	}

	// overloaded in subclasses
	protected List<String> getCommands() {
		return commands;
	}

	// overloaded in subclasses
	protected void execute(String command, String[] options) {
		switch (command) {
		case "login-r":
			loginReceptionist(options);
			break;
		case "login-c":
			loginCustomer(options);
			break;
		case "help":
			help();
			break;
		case "exit":
			exit();
			break;
		}

	}
	
	private void loginReceptionist(String[] options) {
		System.out.println("TODO loginReceptionist");
	}
	
	private void loginCustomer(String[] options) {
		System.out.println("TODO loginCustomer");
	}
	
	private void exit() {
		end = true;
	}

	public void help() {
		System.out.println("List of commands:");
		for (String cmd : getCommands())
			System.out.println("\t" + cmd);
		System.out.println("Type a command to know its syntax");
	}
	
	protected void printRooms(List<Room> rooms) {
		if (rooms == null)
			return;

		String format = "| %-4d | %-8d | %-25s |%n";

		System.out.format("+------+----------+---------------------------+%n");
		System.out.format("| Room | Capacity | Hotel Address             |%n");
		System.out.format("+------+----------+---------------------------+%n");
		for (Room r : rooms)
			System.out.format(format, r.getRoomNumber(), r.getRoomCapacity(), r.getHotel().getAddress());
		System.out.format("+------+----------+---------------------------+%n");
	}

	protected void printReservations(List<Reservation> reservations) {
		if (reservations == null)
			return;

		String format = "| %-4d | %-8d | %-25s | %-10s | %-10s |%n";

		System.out.format("+------+----------+---------------------------+------------+------------+%n");
		System.out.format("| Room | Capacity | Hotel Address             | Check-In   | Check-Out  |%n");
		System.out.format("+------+----------+---------------------------+------------+------------+%n");
		for (Reservation r : reservations)
			System.out.format(format, r.getRoom().getRoomNumber(), r.getRoom().getRoomCapacity(),
					r.getRoom().getHotel().getAddress(), r.getCheckInDate().toString(), r.getCheckOutDate().toString());
		System.out.format("+------+----------+---------------------------+------------+------------+%n");
	}

}
