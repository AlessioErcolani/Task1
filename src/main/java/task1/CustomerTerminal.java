package task1;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CustomerTerminal extends Terminal {

private Customer customer;
	
	private final static List<String> commands = Arrays.asList(
			"show-rooms",
			"help",
			"logout"
			);

	public CustomerTerminal(Customer customer, Scanner scanner) {
		super(scanner);
		this.customer = customer;
	}
	
	public CustomerTerminal(Customer customer) {
		this(customer, new Scanner(System.in));
	}
	
	@Override
	public String getUsername() {
		return customer.getUsername();
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
