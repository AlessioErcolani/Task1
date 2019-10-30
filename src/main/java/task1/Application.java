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
				"help login",
				"login -u r2 -p pwd",
				"login -c -u federico -p pwd",
				"logout",
				"login -r -u r2 -p pwd",
				"show-reservations",
				"show-reservations --from 2018-11-16",
				"show-reservations --hotel 3",
				"show-reservations --hotel 1",
				"logout",
				"exit"
		};
		
		/*Terminal.testCommandLines = new String[] {
				"login-r -u r2 -p pwd",
				"show-reservations",
				"add-reservation -c pippo -f 2020-01-12 -t 2020-01-15 -h 3 -r 201",
				"add-reservation -c chiara -f 2020-01-12 -t 2020-01-15 -h 3 -r 201",
				"add-reservation -c chiara -f 2020-01-12 -t 2020-01-15 -h 3 -r 201",
				"show-reservations",
				"logout",
				"exit"
		};*/
		
		/*Terminal.testCommandLines = new String[] {
				"login-r -u r2 -p pwd",
				"show-rooms",
				"show-rooms --bookable",
				"show-rooms -n",
				"show-rooms -f 2019-11-14",
				"show-rooms -n -f 2019-11-15 -t 2019-11-19",
				"show-rooms -t 2019-11-19",
				"show-rooms -t 2018-11-19",
				"logout",
				"exit"
		};*/
		
		/*Terminal.testCommandLines = new String[] {
				"login-r -u r2 -p pwd",
				"help set-room",
				"show-rooms",
				"set-room --hotel 3 --room 301 --notavailable",
				"show-rooms",
				"set-room --hotel 3 --room 301 --available",
				"show-rooms",
				"set-room --hotel 565 --room 656 --available",
				"logout",
				"exit"
		};*/
		
		/*Terminal.testCommandLines = new String[] {
				"login-r -u r2 -p pwd",
				"help update-reservation",
				"show-reservations -h 3 -f 2019-11-15",
				"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15",
				"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -h 2",
				"show-reservations -h 3",
				//"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -c chiara",
				"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -r 101 -h 2",
				//"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -r 201 -c chiara",
				//"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -r 302 -c chiara", // 302 is not available
				"show-reservations -h 3",
				"show-reservations -h 2",
				"logout",
				"exit"
		};*/
		
		Terminal cli = new Terminal();
		
		while (cli.notEnd()) {
			System.out.print("\n" + cli.getUsername() + "> ");
			
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
