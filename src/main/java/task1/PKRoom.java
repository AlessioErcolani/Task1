package task1;

import java.io.Serializable;

import javax.persistence.*;

@Embeddable
public class PKRoom implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Hotel hotel;	
	private Long roomNumber;
	
	public PKRoom(Hotel hotel, Long roomNumber){
		this.setHotel(hotel);
		this.setRoomNumber(roomNumber);
	}
	
	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Long getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(Long roomNumber) {
		this.roomNumber = roomNumber;
	}
	
	
	
	
	
}