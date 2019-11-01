package task1;

public class Booking {
	
	private String name;
	private String surname;
	private String roomNumber;

	public Booking(String name, String surname, String roomNumber) {
		this.name = name;
		this.surname = surname;
		this.roomNumber = roomNumber;		
	}

	@Override
	public String toString() {
		return "Booking [name = " + name + ", surname = " + surname + ", roomNumber = " + roomNumber + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Booking other = (Booking) obj;
		return 	this.name.equals(other.getName()) &&
				this.surname.equals(other.getSurname()) &&
				this.roomNumber.equals(other.getRoomNumber());
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

}
