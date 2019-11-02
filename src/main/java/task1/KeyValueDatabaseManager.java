package task1;

import org.iq80.leveldb.*;

import exc.BookingAlreadyPresentException;
import exc.DatabaseManagerException;

import static org.fusesource.leveldbjni.JniDBFactory.*;

import java.io.File;
import java.io.IOException;

public class KeyValueDatabaseManager {

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
			throw new DatabaseManagerException("Cannot open key-value database");
		}
	}

	/**
	 * Close the Key-Value database
	 * 
	 * @throws DatabaseManagerException
	 */
	public void closeKeyValueDb() throws DatabaseManagerException {
		try {
			keyValueDb.close();
		} catch (IOException e) {
			throw new DatabaseManagerException("Cannot close key-value DB: " + e.getMessage());
		}
	}

	/**
	 * Checks if the id is already present in the key-value database
	 * 
	 * @param id the unique id of a reservation
	 * @return
	 */
	private boolean isIdAlreadyPresent(String id) {
		DBIterator iterator = keyValueDb.iterator();
		iterator.seek(bytes("Reservation:" + id));
		return iterator.hasNext();
	}

	/**
	 * Forges a new key to be used in key-value database
	 * 
	 * @param id    the unique id of a reservation
	 * @param field to forge the complete key
	 * @return the key bytes
	 */
	private byte[] forgeKey(String id, Type field) {
		String key = "Reservation:" + id + ":";
		switch (field) {
		case NAME:
			key += "Name";
			break;
		case SURNAME:
			key += "Surname";
			break;
		case ROOM_NUMBER:
			key += "RoomNumber";
			break;
		}
		return key.getBytes();
	}

	/**
	 * Inserts a new entry in the key-value database
	 * 
	 * @param id    the unique id of a reservation
	 * @param value the value related to a key
	 * @param field the type of the value
	 * @throws DatabaseManagerException in case of errors
	 */
	private void insertFieldKeyValue(String id, String value, Type field) throws DatabaseManagerException {
		try {
			byte[] key = forgeKey(id, field);
			keyValueDb.put(key, bytes(value));

		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		}

	}

	/**
	 * Deletes the entry that corresponds to the id and the type. If not present the
	 * function does nothing.
	 * 
	 * @param id    the unique id of a reservation
	 * @param field the type to build the key
	 * @throws DatabaseManagerException
	 */
	private void deleteFieldKeyValue(String id, Type field) throws DatabaseManagerException {
		try {
			byte[] key = forgeKey(id, field);
			keyValueDb.delete(key);

		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		}
	}

	/**
	 * Return the entire database as a string. Used to debug.
	 * 
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
	 * 
	 * @param id      the unique id of a reservation
	 * @param booking
	 * @throws DatabaseManagerException
	 * @throws BookingAlreadyPresentException
	 */

	public void insertBooking(String id, Booking booking)
			throws DatabaseManagerException, BookingAlreadyPresentException {

		if (isIdAlreadyPresent(id))
			throw new BookingAlreadyPresentException();

		boolean writesCompleted[] = { false, false };

		try {
			insertFieldKeyValue(id, booking.getName(), Type.NAME);
			writesCompleted[0] = true;
			insertFieldKeyValue(id, booking.getSurname(), Type.SURNAME);
			writesCompleted[1] = true;
			insertFieldKeyValue(id, booking.getRoomNumber(), Type.ROOM_NUMBER);
		} catch (DatabaseManagerException e) {
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
	 * @throws DatabaseManagerException
	 */

	public void deleteBooking(String id) throws DatabaseManagerException {
		deleteFieldKeyValue(id, Type.NAME);
		deleteFieldKeyValue(id, Type.SURNAME);
		deleteFieldKeyValue(id, Type.ROOM_NUMBER);
	}

	/**
	 * Returns a Booking wrapper containing reservation informations
	 * 
	 * @param id the unique id of a reservation
	 * @return a Booking wrapper, null if the id is not present in the database
	 * @throws DatabaseManagerException
	 */
	public Booking getBooking(String id) throws DatabaseManagerException {

		String name = null;
		String surname = null;
		String roomNumber = null;

		try {
			name = asString(keyValueDb.get(forgeKey(id, Type.NAME)));
			surname = asString(keyValueDb.get(forgeKey(id, Type.SURNAME)));
			roomNumber = asString(keyValueDb.get(forgeKey(id, Type.ROOM_NUMBER)));

		} catch (Exception e) {
			throw new DatabaseManagerException(e.getMessage());
		}

		if (name == null || surname == null || roomNumber == null)
			return null;

		return new Booking(id, name, surname, roomNumber);
	}

}
