package task1;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ReceptionistTerminal extends Terminal {
	
	private Receptionist receptionist;
	
	private final static List<String> commands = Arrays.asList(
			"show-rooms",
			"help",
			"logout"
			);

	public ReceptionistTerminal(Receptionist receptionist, Scanner scanner) {
		super(scanner);
		this.receptionist = receptionist;
	}
	
	public ReceptionistTerminal(Receptionist receptionist) {
		this(receptionist, new Scanner(System.in));
	}
	
	@Override
	public String getUsername() {
		return receptionist.getUsername();
	}
	
	@Override
	protected List<String> getCommands() {
		return commands;
	}
	
	@Override
	protected void execute(String command, String[] options) {
		switch (command) {
		case "show-rooms":
			//showRooms();
			break;
		case "help":
			help();
			break;
		case "logout":
			logout();
			break;
		}
	}
	
	private void logout() {
		this.newUser = true;
		this.nextUser = null;
	}

}
