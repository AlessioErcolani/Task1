package task1;

import java.util.logging.Level;

public class Application {
	
	public static HotelManager hotelDatabaseManager;

	public static void main(String[] args) {
		
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
		
		System.out.println("Populating database...");
		Application.hotelDatabaseManager = new HotelManager("hotel_chain");
		HotelManager.populateDatabase(hotelDatabaseManager);
		
		System.out.println("\nType commands to use the application");
		
		boolean testing = true;
		Terminal.testCommandLines = new String[] {
				"login-r -u r2 -p pwd",
				"show-reservations",
				"logout",
				"exit"
		};
		Terminal.testCommandLines = new String[] {
				"login-r -u r2 -p pwd",
				"show-reservations",
				"show-reservations --from 2018-11-16",
				"show-reservations --hotel 3",
				"show-reservations --hotel 1",
				"logout",
				"exit"
		};
		
		Terminal cli = new Terminal();
		
		while (cli.notEnd()) {
			System.out.print(cli.getUsername() + "> ");
			
			String command = null;
			if (testing)
				command = Terminal.nextCommand();
			else
				command = cli.readCommand();
			
			cli.executeCommandLine(command);
			
			if (cli.hasNewUser())
				cli = cli.switchTerminal();
		}
		
		System.out.println("\nClosing application");

	}

}
