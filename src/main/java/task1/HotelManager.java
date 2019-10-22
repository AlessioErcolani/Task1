package task1;

import javax.persistence.*;
import org.hibernate.exception.ConstraintViolationException;

public class HotelManager {

	private EntityManagerFactory factory;
	private EntityManager entityManager;
	
	public HotelManager(String databaseSchema) {
		factory = Persistence.createEntityManagerFactory(databaseSchema);
	}
	
	public void exit() {
		factory.close();
	}
	
	private void setup() {
		entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();
	}
	
	private void commit() {
		entityManager.getTransaction().commit();
	}
	
	private void persistObject(Object obj) {		
		entityManager.persist(obj);		
	}
	
	private void mergeObject(Object obj) {
		entityManager.merge(obj);
	}

	public void createCustomer(Customer customer) throws UniqueConstraintException, DatabaseManagerException {
		try {
			setup();
			persistObject(customer);
			commit();
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
	
	public Customer authenticateCustomer(String username, String password) throws DatabaseManagerException {
		Customer customer = null;
		try {
			setup();
			TypedQuery<Customer> query = entityManager.createNamedQuery("Customer.findByUsernameAndPassword", Customer.class);
			query.setParameter("username", username);
			query.setParameter("password", password);
			customer = query.getSingleResult();
			commit();
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
			setup();
			persistObject(hotel);
			commit();
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
	}
	
	public Hotel readHotel(String address) throws DatabaseManagerException {
		Hotel hotel = null;
		try {			
			setup();
			TypedQuery<Hotel> query = entityManager.createNamedQuery("Hotel.findByAddress", Hotel.class);
			query.setParameter("address", address);
			hotel = query.getSingleResult();
			commit();	
		} catch (NoResultException nr) {
			return null;
		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
		return hotel;
	}

	public void createRoom(String address, Room room) throws DatabaseManagerException {
		try {
			Hotel hotel = readHotel(address);
			if (hotel == null) {
				throw new ForeignKeyException("Hotel not found");
			}
			
			setup();
			room.setHotel(hotel);
			mergeObject(room);
			commit();

		} catch (Exception ex) {
			throw new DatabaseManagerException(ex.getMessage());
		} finally {
			entityManager.close();
		}
	}
	
	

	public static void main(String[] args) {

		HotelManager manager = new HotelManager("hotel_chain");
		
		try {
			Customer customer = new Customer("cocorita", "96", "Marco", "Del Gamba");
			//Customer customer = new Customer("cocorita", "svrizzi", "Alessio", "Ercolani");
			try {
				manager.createCustomer(customer);
				
				System.out.println("Authenticate Customer");
				Customer authCustomer = manager.authenticateCustomer("cocorita", "96");
				if (authCustomer == null)
					System.out.println("Customer not found");
				else
					System.out.println("Id customer " + authCustomer.getID());
				
			} catch (UniqueConstraintException ue) {
				System.out.println(ue.getMessage());
			}

			 Hotel hotel = new Hotel("Pisa,Pratale,2");
			 manager.createHotel(hotel);
			
			 Room room = new Room(1, 2);
			 manager.createRoom("Pisa,Pratale,2", room);
		} catch (DatabaseManagerException de) {
			System.out.println(de.getMessage());
		}
		manager.exit();
	}
}
