package task1;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

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
    
    public void createCustomer(Customer customer){
    	
        try{   
        	entityManager = factory.createEntityManager();
        	entityManager.getTransaction().begin();
        	entityManager.persist(customer);
        	entityManager.getTransaction().commit();
        	
        }catch (PersistenceException pe) { //ConstraintViolationException ce
		 	Throwable t = pe.getCause();
		    while ((t != null) && !(t instanceof ConstraintViolationException)) {
		        t = t.getCause();
		    }
		    if (t instanceof ConstraintViolationException) {
		    	//TODO: trow my exeption		    
		        System.out.println("Duplicate");
		    }
        }  
        catch (Exception ex) {
        		ex.printStackTrace();
        } finally {
        	entityManager.close();
		}   
    }
    
    public void deleteCustomerByID(Long customerID) {    
    	try{
			entityManager = factory.createEntityManager();
		    entityManager.getTransaction().begin();
		    Customer customer = entityManager.getReference(Customer.class, customerID);          
		    entityManager.remove(customer);
			entityManager.getTransaction().commit();
    	}catch (Exception ex) {
    		ex.printStackTrace(); 
    	}finally{
    		entityManager.close();
    	}
    }
    
  /*  public void readProfessor(long professorId) {
  
            System.out.println("Getting the Courses");
            
            try {
    	    	entityManager = factory.createEntityManager();
    	        entityManager.getTransaction().begin();
    	        List<Course> courses = entityManager.createQuery(
    	        	    "select c " +
    	        	    "from Course c " +
    	        	    "where c.professor.id = :professorId", Course.class)
    	        	.setParameter( "professorId", 1L )
    	        	.getResultList();
    	        entityManager.getTransaction().commit();
    	        
    	        for(int i = 0; i < courses.size(); i++) {
    	            System.out.println("CFU : " + courses.get(i).getCFU());
    	            System.out.println("Evaluation : " + courses.get(i).getEvaluation());
    	        }

                
    		} catch (Exception ex) {
    			ex.printStackTrace();
    			System.out.println("A problem occurred in retriving the courses!");

    		} finally {
    			entityManager.close();
    		}       

         
        }
     
    }*/
    public void createHotel(Hotel hotel){
    	try{   
        	entityManager = factory.createEntityManager();
        	entityManager.getTransaction().begin();
        	entityManager.persist(hotel);
        	entityManager.getTransaction().commit();
        	
        } catch (Exception ex) {
        	ex.printStackTrace();
        } finally {
        	entityManager.close();
		}   
    	
    }
    
    public void createRoom(String city, String street, int streetNumber, Room room) {
    	try{ 
    		//NOT EFFICENT hotel is different from reference
        	Hotel hotel = readHotel(city, street, streetNumber);
        	if (hotel == null){
        		System.out.println("hotel not found");
        		return;
        	}
        	entityManager = factory.createEntityManager();
        	entityManager.getTransaction().begin();
        	Hotel reference = entityManager.find(Hotel.class, hotel.getID_hotel());
        
        	room.setHotel(reference);
        	reference.addRoom(room);
        	entityManager.persist(reference);
        	entityManager.getTransaction().commit();
        	
        } catch (Exception ex) {
        	ex.printStackTrace();
        } finally {
        	entityManager.close();
		} 
    }
    
    public Hotel readHotel(String city, String street, int streetNumber ) {
    	Hotel hotel = null;
        try {
	    	entityManager = factory.createEntityManager();
	        entityManager.getTransaction().begin();	
	        TypedQuery<Hotel> query = entityManager.createQuery(
	        	    "SELECT h " +
	        	    "FROM Hotel h " +
	        	    "WHERE h.city = :cityToSearch AND " +
	        	    "h.street = :streetToSearch AND " + 
	        	    "h.streetNumber = :streetNumberToSearch"
	        	    , Hotel.class);
	        query.setParameter("cityToSearch", 			city);
	        query.setParameter("streetToSearch", 		street);
	        query.setParameter("streetNumberToSearch", 	streetNumber);
	        System.out.println("Dopo della create query");
	        hotel = query.getSingleResult();
	        entityManager.getTransaction().commit();
        } catch( NoResultException nr)
        {
        	return null;
        }
        catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			entityManager.close();
		}
        return hotel;
    }
    
    public void deleteHotelByID(Long hotelID) {    
    	try{
			entityManager = factory.createEntityManager();
		    entityManager.getTransaction().begin();
		    Hotel hotel = entityManager.getReference(Hotel.class, hotelID);          
		    entityManager.remove(hotel);
			entityManager.getTransaction().commit();
    	}catch (Exception ex) {
    		ex.printStackTrace(); 
    	}finally{
    		entityManager.close();
    	}
    }
    
    public void insertReservation(Reservation reservation) {
    	try{   
        	entityManager = factory.createEntityManager();
        	entityManager.getTransaction().begin();
        	entityManager.persist(reservation);
        	entityManager.getTransaction().commit();
        	
        } catch (Exception ex) {
        	ex.printStackTrace();
        } finally {
        	entityManager.close();
		}   
    	
    }
    
/*   public void deleteRoomByID(Long hotelID) {    
    	try{
			entityManager = factory.createEntityManager();
		    entityManager.getTransaction().begin();
		    Hotel hotel = entityManager.getReference(Hotel.class, hotelID);          
		    entityManager.remove(hotel);
			entityManager.getTransaction().commit();
    	}catch (Exception ex) {
    		ex.printStackTrace(); 
    	}finally{
    		entityManager.close();
    	}
    }*/
    
    
   
    public static void main(String[] args) {
    	
    	HotelManager manager = new HotelManager();
        manager.setup();
        
        //Customer customer = new Customer("cocorita","96", "Marco", "Del Gamba");  
        //manager.createCustomer(customer);
        
        //Hotel hotel = new Hotel("Pisa", "Cenaia", 44);
        //manager.createHotel(hotel);
         
    
       Room room = new Room(1, 2);       
       manager.createRoom("Pissa", "Cenaia", 44, room);
        
        
        manager.exit();  	 
    }

}
