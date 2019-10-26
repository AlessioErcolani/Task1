package task1;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import exc.DatabaseManagerException;

public class CustomerTerminal extends Terminal {

	private Customer customer;
	
	private final static List<String> commands = Arrays.asList(
			"show-reservations",
			"help",
			"logout"
			);

	private final static Map<String, Options> optionsMap;
	
	static {
		Map<String, Options> map = new HashMap<>();
		
		map.put("show-reservations", getOptionsForShowReservations());
		map.put("help", new Options());
		map.put("logout", new Options());
		
		optionsMap = map;
	}
	
	//------------------------------------------------------------------------\\
	// Constructors                                                           \\
	//------------------------------------------------------------------------\\
	
	public CustomerTerminal(Customer customer, Scanner scanner) {
		super(scanner);
		this.customer = customer;
	}
	
	public CustomerTerminal(Customer customer) {
		this(customer, new Scanner(System.in));
	}
	
	//------------------------------------------------------------------------\\
	// @Override methods                                                      \\
	//------------------------------------------------------------------------\\
	
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
	
	private void showReservations(String[] options) {
		try {
        	CommandLine cmd = parser.parse(getOptionsMap().get("show-reservations"), options);
            
            /*if (cmd.hasOption("all"))
            	printReservations(customer.getReservations());
            else if (cmd.hasOption("past"))
            	printReservations(customer.getReservations());
            else*/
            	printReservations(customer.getUpcomingReservations());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("show-reservations", getOptionsMap().get("show-reservations"));
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

	private static Options getOptionsForShowReservations() {
		Options options = new Options();
		
		/*Option all = new Option("a", "all", false, "show all reservations");
		Option past = new Option("p", "past", false, "show only past reservations");
		
		OptionGroup group = new OptionGroup();
		group.addOption(all);
		group.addOption(past);
		group.setRequired(false);
		
		options.addOption(all);
		options.addOption(past);
		options.addOptionGroup(group);*/
        
        return options;
	}
	
}
