package task1;

import org.iq80.leveldb.*;

import exc.*;

import static org.fusesource.leveldbjni.JniDBFactory.*;

import java.io.*;

public class KeyValueDatabaseManager {
	
	// used to simulate the key-value database down
	public boolean isAvailable = true;

	private DB keyValueDb;

	private enum Type {
		NAME, SURNAME, ROOM_NUMBER
	}

	public KeyValueDatabaseManager(String databaseName) throws DatabaseManagerException {
		Options options = new Options();
		options.createIfMissing(true);

		try {
			keyValueDb = org.fusesource.leveldbjni.JniDBFactory.factory.open(new File("reservations"), options);
		} catch (IOException e) {
			throw new KeyValueDatabaseManagerException("Cannot open key-value database");
		}
	}

	/**
	 * Close the Key-Value database
	 * @throws KeyValueDatabaseManagerException
	 */
	public void closeKeyValueDb() throws KeyValueDatabaseManagerException {
		try {
			keyValueDb.close();
		} catch (IOException e) {
			throw new KeyValueDatabaseManagerException("Cannot close key-value DB: " + e.getMessage());
		}
	}

	/**
	 * Checks if the id is already present in the key-value database
	 * @param id the unique id of a reservation
	 * @return true if id is already present, false otherwise
	 */
	public boolean isIdAlreadyPresent(String id) {
		
		byte[] keyName = forgeKey(id, Type.NAME);
		byte[] keySurname = forgeKey(id, Type.SURNAME);
		byte[] keyRoomNumber = forgeKey(id, Type.ROOM_NUMBER);
		
		String valueName = asString(keyValueDb.get(keyName));
		String valueSurname = asString(keyValueDb.get(keySurname));
		String valueRoomNumber = asString(keyValueDb.get(keyRoomNumber));
				
		return 	valueName != null ||
				valueSurname != null ||
				valueRoomNumber != null;
	}

	/**
	 * Forges a new key to be used in key-value database
	 * @param id    the unique id of a reservation
	 * @param field to forge the complete key
	 * @return the key in bytes
	 */
	private byte[] forgeKey(String id, Type field) {
		String key = "res:" + id + ":";
		switch (field) {
		case NAME:
			key += "name";
			break;
		case SURNAME:
			key += "surname";
			break;
		case ROOM_NUMBER:
			key += "room";
			break;
		}
		return key.getBytes();
	}

	/**
	 * Inserts a new entry in the key-value database
	 * @param id    the unique id of a reservation
	 * @param value the value related to a key
	 * @param field the type of the value
	 * @throws KeyValueDatabaseManagerException in case of errors
	 */
	private void insertFieldKeyValue(String id, String value, Type field) throws KeyValueDatabaseManagerException {
		try {
			byte[] key = forgeKey(id, field);
			keyValueDb.put(key, bytes(value));
		} catch (Exception e) {
			throw new KeyValueDatabaseManagerException(e.getMessage());
		}

	}

	/**
	 * Deletes the entry that corresponds to the id and the type. If not present the
	 * function does nothing.
	 * 
	 * @param id    the unique id of a reservation
	 * @param field the type to build the key
	 * @throws KeyValueDatabaseManagerException
	 */
	private void deleteFieldKeyValue(String id, Type field) throws KeyValueDatabaseManagerException {
		try {
			byte[] key = forgeKey(id, field);
			keyValueDb.delete(key);
		} catch (Exception e) {
			throw new KeyValueDatabaseManagerException(e.getMessage());
		}
	}

	/**
	 * Return the entire database as a string. Used to debug.
	 * @return
	 */
	public String toStringKeyValue() {
		String string = "";

		DBIterator iterator = keyValueDb.iterator();
		iterator.seekToFirst();
		while (iterator.hasNext()) {
			string += asString(iterator.peekNext().getKey()) + " = ";
			string += asString(iterator.peekNext().getValue());
			string += "\n";
			iterator.next();
		}

		return string;
	}
	
	/**
	 * Inserts a new Booking wrapper in the key-value database
	 * @param id the unique id of a reservation
	 * @param booking
	 * @throws KeyValueDatabaseManagerException
	 * @throws BookingAlreadyPresentException
	 */
	public void insertBooking(String id, Booking booking)
			throws KeyValueDatabaseManagerException, BookingAlreadyPresentException {

		if (isIdAlreadyPresent(id)) 
			throw new BookingAlreadyPresentException(id);

		boolean writesCompleted[] = { false, false };

		try {
			insertFieldKeyValue(id, booking.getName(), Type.NAME);
			writesCompleted[0] = true;
			insertFieldKeyValue(id, booking.getSurname(), Type.SURNAME);
			writesCompleted[1] = true;
			insertFieldKeyValue(id, booking.getRoomNumber(), Type.ROOM_NUMBER);
		} catch (KeyValueDatabaseManagerException e) {
			if (writesCompleted[0]) {
				deleteFieldKeyValue(id, Type.NAME);
				if (writesCompleted[1])
					deleteFieldKeyValue(id, Type.SURNAME);
			}
			throw e;
		}
	}

	/**
	 * Deletes a booking on the key-value database. If the id is not present in the
	 * database the function does nothing
	 * 
	 * @param id the unique id of a reservation
	 * @throws KeyValueDatabaseManagerException
	 */
	public void deleteBooking(String id) throws KeyValueDatabaseManagerException {
		try {
			deleteFieldKeyValue(id, Type.NAME);
			deleteFieldKeyValue(id, Type.SURNAME);
			deleteFieldKeyValue(id, Type.ROOM_NUMBER);
		} catch (Exception e) {
			throw new KeyValueDatabaseManagerException();
		}
	}

	/**
	 * Returns a Booking wrapper containing reservation informations
	 * 
	 * @param id the unique id of a reservation
	 * @return a Booking wrapper
	 * @throws DatabaseManagerException
	 * @throws BookingNotFoundException 
	 */
	public Booking getBooking(String id) throws KeyValueDatabaseManagerException, BookingNotFoundException {

		String name = null;
		String surname = null;
		String roomNumber = null;

		try {
			name = asString(keyValueDb.get(forgeKey(id, Type.NAME)));
			surname = asString(keyValueDb.get(forgeKey(id, Type.SURNAME)));
			roomNumber = asString(keyValueDb.get(forgeKey(id, Type.ROOM_NUMBER)));

		} catch (Exception e) {
			throw new KeyValueDatabaseManagerException(e.getMessage());
		}

		if (name == null || surname == null || roomNumber == null)
			throw new BookingNotFoundException();

		return new Booking(id, name, surname, roomNumber);
	}
}
