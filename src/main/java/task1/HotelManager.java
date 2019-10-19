package task1;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HotelManager {
	private EntityManagerFactory factory;
    private EntityManager entityManager;
 
    public void setup() {
    	 factory = Persistence.createEntityManagerFactory("hotel_chain");
    }
 
    public void exit() {
    	factory.close();
    }
   
    public static void main(String[] args) {
    	
    	HotelManager manager = new HotelManager();
        manager.setup();       
       

        manager.exit();
        System.out.println("Finished");   	 
    }

}
