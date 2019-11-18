package task1;

import java.util.logging.Level;

import exc.DatabaseManagerException;

public class Application {
	
	public static DatabaseManager hotelDatabaseManager;

	public static void main(String[] args) {		
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
		
		try {
			Application.hotelDatabaseManager = new DatabaseManager("hotel_chain");
		} catch (DatabaseManagerException e) {
			System.out.println(e.getMessage());
		}
		// System.out.println("Populating database...");
		// DatabaseManager.populateDatabase(hotelDatabaseManager);
		// System.out.println(Application.hotelDatabaseManager.keyValue.toStringKeyValue());
		
		System.out.println("\nType commands to use the application");
		
		boolean testing = false;
		
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
		
		hotelDatabaseManager.exit();
		
		System.out.println("\nClosing application");

	}

}

/*Terminal.testCommandLines = new String[] {
"login --receptionist -u r2 -p pwd",
"help register",
"register -n Pino -s Verdi -u pino -p pwd",
"register -n Pino -s Verdi -u pino",
"register -n Pino -s Verdi -u federico",
"logout",
"login -c -u pino -p pwd",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login -c -u piergiorgio -p pwd",
"help change-password",
"change-password",
"change-password -n new",
"logout",
"login -c -u piergiorgio -p pwd",
"login -c -u piergiorgio -p new",
"change-password -n pwd",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login --receptionist -u r2 -p pwd",
"show-reservations -f 2000-01-01",
"check-in --id 656",
"check-in -i 2",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login --receptionist -u r2 -p pwd",
"sim-key-value --disable",
"add-reservation -c chiara -f 2020-01-12 -t 2020-01-15 -h 3 -r 201",
"update-reservation --currenthotel 3 --currentroom 201 --currentcheckin 2020-01-12 -c alessio",	
"check-in --id 25",
"delete-reservation -d 2020-01-12 -h 3 -r 201",
"sim-key-value --enable",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login --receptionist -u r2 -p pwd",
"sim-key-value --disable",
"check-in --id 2",
"check-out --id 2",
"sim-key-value --enable",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login --receptionist -u r2 -p pwd",
"show-reservations -f 2000-01-01",
"check-in --id 1",
"add-reservation -c chiara -f 2020-01-12 -t 2020-01-15 -h 3 -r 201",
"show-reservations -f 2000-01-01",
"check-in --id 1",
"delete-reservation -d 2020-01-12 -h 3 -r 201",
"show-reservations -f 2000-01-01",
"check-in --id 1",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login --receptionist -u r2 -p pwd",
"show-reservations -f 2000-01-01",
"add-reservation -c chiara -h 3 -r 201 -f 2019-11-07",
"show-reservations -f 2000-01-01",
"check-in --id 3",
"check-out --id 3",
"check-in --id 3",
"check-out --id 3",
"delete-reservation -h 3 -r 201 -d 2019-11-07",
"show-reservations -f 2000-01-01",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login --receptionist -u r2 -p pwd",
"show-reservations",
"add-reservation -c chiara -f 2020-01-12 -t 2020-01-15 -h 3 -r 201",
"show-reservations",
"delete-reservation -d 2020-01-12 -h 3 -r 201",
"delete-reservation -d 2020-01-12 -h 3 -r 201",	// delete a reservation that does not exist!
"show-reservations",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"help login",
"help help",
"help exit",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login --customer -u federico -p pwd",
"help show-reservations",
"help show-hotels",
"help show-rooms",
"help help",
"help logout",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login --receptionist -u r2 -p pwd",
"help show-hotels",
"help show-rooms",
"help add-reservation",
"help show-reservations",
"help update-reservation",
"help delete-reservation",
"help set-room",
"help register",
"help help",
"help logout",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login -c -u piergiorgio -p pwd",
"help",
"help show-reservations",
"show-hotels",
"show-reservations",
"show-rooms -h 3 -n -f 2019-11-15 -t 2019-11-19",
"show-rooms -h 3 -t 2019-11-19",
"show-rooms -h 3 -t 2018-11-19",
"show-rooms -h 1",
"show-rooms -h 2",
"show-rooms -h 3",
"show-rooms -h 3 --all",
"show-rooms -h 3 -n",
"show-rooms -h 3 -b",
"help show-rooms",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"help login",
"login -u r2 -p pwd",
"login -c -u federico -p pwd",
"logout",
"login -r -u r2 -p pwd",
"show-reservations",
"show-reservations --from 2018-11-16",
"show-reservations --from 2018-11-15",
"show-reservations --hotel 3",
"show-reservations --hotel 1",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login -r -u r2 -p pwd",
"show-reservations",
"add-reservation -c pippo -f 2020-01-12 -t 2020-01-15 -h 3 -r 201",
"add-reservation -c chiara -f 2020-01-12 -t 2020-01-15 -h 3 -r 201",
"add-reservation -c chiara -f 2020-01-12 -t 2020-01-15 -h 3 -r 201",
"show-reservations",
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -f 2019-11-12 -t 2019-11-15",
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-12 -c chiara",
"show-reservations",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login -r -u r2 -p pwd",
"show-rooms",
"show-rooms --bookable",
"show-rooms -n",
"show-rooms -f 2019-11-14",
"show-rooms -n -f 2019-11-15 -t 2019-11-19",
"show-rooms -t 2019-11-19",
"show-rooms -t 2018-11-19",
"show-rooms -a",
"show-rooms -a -h 1",
"show-rooms -a -h 2",
"show-rooms --all -h 3",
"show-rooms --all -h 4",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login -r -u r2 -p pwd",
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
"login -r -u r2 -p pwd",
"show-reservations -h 3 -f 2019-11-15",
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -h 2",
"show-reservations -h 3",
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -c chiara",
"show-reservations -h 3",
//"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -r 101 -h 2",
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -r 201 -c chiara",
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -r 302 -c chiara", 		// 302 is not available
"show-reservations -h 3",
"show-reservations -h 2",
"update-reservation --currenthotel 2 --currentroom 101 --currentcheckin 2019-11-15 -r 401 -h 3 -c piergiorgio",	// reset changes
"show-reservations -h 3",
"show-reservations -h 2",
"logout",
"exit"
};*/

/*Terminal.testCommandLines = new String[] {
"login -r -u r2 -p pwd",
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -r 201 -c chiara",
"update-reservation --currenthotel 3 --currentroom 201 --currentcheckin 2019-11-15 -r 401 -c piergiorgio",		// reset changes
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -c federico",				// key is untouched!
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -c piergiorgio",				// reset changes
"update-reservation --currenthotel 3 --currentroom 201 --currentcheckin 2019-11-15 -r 401",						// old reservation does not exist!
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -r 656",						// new room does not exist!
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -c idonotexist",				// new customer does not exist!
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -c piergiorgio",				// it stays the same!
"show-rooms --all -h 3",
"update-reservation --currenthotel 3 --currentroom 401 --currentcheckin 2019-11-15 -r 302 -c chiara", 			// 302 is not available!
"show-rooms -b -h 3 -f 2019-11-15",
"show-rooms -n -h 3 -f 2019-11-15",
"show-reservations -h 3",
"show-reservations -h 2",
"logout",
"exit"
};*/