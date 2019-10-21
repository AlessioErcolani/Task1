package task1;

import java.io.Serializable;

import javax.persistence.*;

@Embeddable
public class PKRoom implements Serializable {

	private static final long serialVersionUID = 1L;
	private Hotel hotel;
	private int roomNumber;

	public PKRoom() {

	}

	public PKRoom(Hotel hotel, int roomNumber) {
		this.setHotel(hotel);
		this.setRoomNumber(roomNumber);
	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}

}