package task1;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Terminal {
	private User user;
	private boolean end;
	private Scanner input;

	public Terminal() {
		user = null;
		input = new Scanner(System.in);
	}
	
	public boolean notEnd() {
		return !end;
	}
	
	public String getUsername() {
		if (user == null)
			return "";
		else
			return user.getUsername();
	}
	
	public String readCommand() {
		String commandLine = input.nextLine();
		
		return commandLine;
	}
	
	public void executeCommandLine(String input) {
		String[] splitCommandLine = input.split(" ");
		
		// first word is the command name
		String command = splitCommandLine[0];
		
		// other words are the options
		String[] options = Arrays.copyOfRange(splitCommandLine, 1, splitCommandLine.length);
		
		// TODO: execute true commands
		System.out.println("Command: " + command);
		System.out.println("Options:");
		for (String o : options)
			System.out.println("\t" + o);
		end = true;
		
	}
	
	public void printRooms(List<Room> rooms) {
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
	
	public void printReservations(List<Reservation> reservations) {
		if (reservations == null)
			return;
		
		String format = "| %-4d | %-8d | %-25s | %-10s | %-10s |%n";

		System.out.format("+------+----------+---------------------------+------------+------------+%n");
		System.out.format("| Room | Capacity | Hotel Address             | Check-In   | Check-Out  |%n");
		System.out.format("+------+----------+---------------------------+------------+------------+%n");
		for (Reservation r : reservations)
			System.out.format(format,
					r.getRoom().getRoomNumber(),
					r.getRoom().getRoomCapacity(),
					r.getRoom().getHotel().getAddress(),
					r.getCheckInDate().toString(),
					r.getCheckOutDate().toString());
		System.out.format("+------+----------+---------------------------+------------+------------+%n");
	}

}

