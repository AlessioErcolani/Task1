package task1;

import javax.persistence.*;

@MappedSuperclass
public abstract class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long ID;
	private String username;
	private String password;
	private String name;
	private String surname;
	
	User(){}
	
	public Long getID() {
		return ID;
	}
	
	public void setID(Long ID) {
		this.ID = ID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}	

}