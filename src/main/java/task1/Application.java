package task1;

import java.util.logging.Level;

public class Application {
	
	public static HotelManager hotelDatabaseManager;

	public static void main(String[] args) {
		
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
		
		System.out.println("Populating database...");
		Application.hotelDatabaseManager = new HotelManager("hotel_chain");
		HotelManager.populateDatabase(hotelDatabaseManager);
		
		Terminal cli = new Terminal();
		
		System.out.println("\nType commands to use the application");
		
		while (cli.notEnd()) {
			System.out.print(cli.getUsername() + "> ");
			String command = cli.readCommand();
			cli.executeCommandLine(command);
		}
		
		System.out.println("\nClosing application");

	}

}
