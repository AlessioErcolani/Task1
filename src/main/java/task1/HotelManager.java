package task1;

import javax.persistence.*;
import org.hibernate.exception.ConstraintViolationException;

public class HotelManager {

	private EntityManagerFactory factory;
	private EntityManager entityManager;

	public void setup() {
		factory = Persistence.createEntityManagerFactory("hotel_chain");
	}

	public void exit() {
		factory.close();
	}

	public void createCustomer(Customer customer) throws UniqueConstraintException, DatabaseManagerException {
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(customer);
			entityManager.getTransaction().commit();

		} catch (PersistenceException pe) { // ConstraintViolationException
			Throwable t = pe.getCause();
			while ((t != null) && !(t instanceof ConstraintViolationException)) {
				t = t.getCause();
			}
			if (t instanceof ConstraintViolationException) {
				throw new UniqueConstraintException(
						"Customer with username: " + customer.getName() + " already present");
			}
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
	}

	public Customer readCustomer(String username) throws DatabaseManagerException {
		Customer customer = null;
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();
			TypedQuery<Customer> query = entityManager.createQuery(
										"SELECT c " + 
										"FROM Customer c " + 
										"WHERE c.username = :usernameToSearch",							
										Customer.class);
			query.setParameter("usernameToSearch", username);
			customer = query.getSingleResult();
			entityManager.getTransaction().commit();
		} catch (NoResultException nr) {
			return null;
		} catch (Exception ex) {			
			 throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
		return customer;
	}

	public void createHotel(Hotel hotel) throws DatabaseManagerException {
		try {
			
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.persist(hotel);
			entityManager.getTransaction().commit();

		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
	}
	
	public Hotel readHotel(String city, String street, int streetNumber) throws DatabaseManagerException {
		Hotel hotel = null;
		try {
			
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();
			TypedQuery<Hotel> query = 	entityManager.createQuery(
										"SELECT h " + 
										"FROM Hotel h " + 
										"WHERE h.city = :cityToSearch AND "
										+ "h.street = :streetToSearch AND " + 
										"h.streetNumber = :streetNumberToSearch",
										Hotel.class);
			query.setParameter("cityToSearch", city);
			query.setParameter("streetToSearch", street);
			query.setParameter("streetNumberToSearch", streetNumber);
			hotel = query.getSingleResult();
			entityManager.getTransaction().commit();
			
		} catch (NoResultException nr) {
			return null;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
		return hotel;
	}

	public void createRoom(String city, String street, int streetNumber, Room room) throws DatabaseManagerException {
		try {
			Hotel hotel = readHotel(city, street, streetNumber);
			if (hotel == null) {
				throw new DatabaseManagerException("Hotel not found");
			}
			
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();
			room.setHotel(hotel);
			entityManager.merge(room);
			entityManager.getTransaction().commit();

		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
	}
	
	public Customer authenticateCustomer(String username, String password) throws DatabaseManagerException {
		Customer customer = null;
		try {
			entityManager = factory.createEntityManager();
			entityManager.getTransaction().begin();
			TypedQuery<Customer> query = entityManager.createNamedQuery("Customer.findByUsernameAndPassword", Customer.class);
			query.setParameter("username", username);
			query.setParameter("password", password);
			customer = query.getSingleResult();
			entityManager.getTransaction().commit();
		} catch (NoResultException nr) {
			return null;
		} catch (Exception ex) {			
			 throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
		return customer;
	}

	public static void main(String[] args) {

		HotelManager manager = new HotelManager();
		manager.setup();

		try {
			Customer customer = new Customer("cocorita", "96", "Marco", "Del Gamba");
			//Customer customer = new Customer("cocorita", "svrizzi", "Alessio", "Ercolani");
			try {
				manager.createCustomer(customer);
				
				System.out.println("Read Customer");
				Customer readCustomer = manager.readCustomer("cocorita");
				if (readCustomer == null)
					System.out.println("Customer not found");
				else
					System.out.println("Id customer " + readCustomer.getID());
				
				System.out.println("Authenticate Customer");
				Customer authCustomer = manager.authenticateCustomer("cocorita", "96");
				if (authCustomer == null)
					System.out.println("Customer not found");
				else
					System.out.println("Id customer " + authCustomer.getID());
				
			} catch (UniqueConstraintException ue) {
				System.out.println(ue.getMessage());
			}

			 //Hotel hotel = new Hotel("Pisa", "Cenaia", 44);
			 //manager.createHotel(hotel);

			 //Room room = new Room(1, 2);
			 //manager.createRoom("Pisa", "Cenaia", 44, room);
		} catch (DatabaseManagerException de) {
			System.out.println(de.getMessage());
		}
		manager.exit();
	}
}
