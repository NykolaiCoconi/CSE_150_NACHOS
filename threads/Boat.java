package nachos.threads;
import nachos.ag.BoatGrader;
import java.util.LinkedList;

public class Boat
{
    static BoatGrader bg;
    static boolean notDone;
    static boolean boatIsOnOahu;
    static boolean rowersToMolokai;
    static Lock lock;
    static int childrenOnBoat;
    static LinkedList<KThread> waitingQueue;
    
    //Condition variables
    static Condition adultOnOahu;
    static Condition adultOnMolokai;
    static Condition childOnOahu;
    static Condition childOnMolokai;
    static Condition onBoat;

    //Our global variables:
    static int initialChildren;
    static int initialAdults;
    
    static int childrenOnOahu;
    static int adultsOnOahu;
    static int childrenOnMolokai;
    static int adultsOnMolokai;
    //NOTE: these are not used by the threads, they will store a local variable
    // with last known number of people on each island!
    
    static int boatLocation; // 0 = Oahu, 1 = Molokai
    
    
    public static void selfTest()
    {
	BoatGrader b = new BoatGrader();
	
	System.out.println("\n ***Testing Boats with only 2 children***");
	begin(0, 2, b);

//	System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
//  	begin(1, 2, b);

//  	System.out.println("\n ***Testing Boats with 3 children, 3 adults***");
//  	begin(3, 3, b);
    }

    public static void begin( int adults, int children, BoatGrader b )
    {
	// Store the externally generated autograder in a class
	// variable to be accessible by children.
	bg = b;

	// Instantiate global variables here
	notDone = true;
	boatIsOnOahu = true;
	lock = new Lock();
	childrenOnBoat = 0;
	
	initialAdults = adults;
	initialChildren = children;
	
	childrenOnOahu = initialChildren;
	adultsOnOahu = initialAdults;
	childrenOnMolokai = 0;
	adultsOnMolokai = 0;

    adultOnOahu = new Condition(lock);
    adultOnMolokai = new Condition(lock);
    childOnOahu = new Condition(lock);
    childOnMolokai = new Condition(lock);
    onBoat = new Condition(lock);
    
    boatLocation = 0; //boat starts on Oahu, otherwise literally nothing works
    //not needed if just using boatIsOnOahu
    
    waitingQueue = new LinkedList<KThread>();
	
	
	// Create threads here. See section 3.4 of the Nachos for Java
	// Walkthrough linked from the projects page.

	Runnable r = new Runnable() {
	    public void run() {
                SampleItinerary();
            }
    };
    KThread t = new KThread(r);
    t.setName("Sample Boat Thread");
    t.fork();

    Runnable r_child = new Runnable() {
    	public void run() {
    		ChildItinerary();
    	}
    }; //r_child
    
    Runnable r_adult = new Runnable() {
	   public void run() {
		   AdultItinerary();
	   }
   	}; //r_adult
   
   	//Spawn Child threads
   	for(int i = 0; i < children; i++) {
	   new KThread(r_child).setName("Child" + Integer.toString(i+1)).fork();
   	}
   	//Spawn Adult threads
   	for(int i = 0; i < adults; i++) {
   		new KThread(r_adult).setName("Adult" + Integer.toString(i+1)).fork();
   	}
   	
   	//hold thread while grader runs
   	while(notDone)
   			KThread.yield();
	   	
    }

    static void AdultItinerary()
    {
	/* This is where you should put your solutions. Make calls
	   to the BoatGrader to show that it is synchronized. For
	   example:
	       bg.AdultRowToMolokai();
	   indicates that an adult has rowed the boat across to Molokai
	*/
    	lock.acquire();
    	
    	while(notDone) {
    		while(!boatIsOnOahu || childrenOnOahu >=2) {
    			adultOnOahu.sleep();
    		}
    		// Row to Molokai!
    		adultsOnOahu--;
    		bg.AdultRowToMolokai();
    		adultsOnMolokai++;
    		boatIsOnOahu = false;
    		
    	}
    }

    static void ChildItinerary()
    {
    	boolean onOahu = true;
    	lock.acquire();
    	
    	while(notDone) {
    		while(!boatIsOnOahu) {
    			//code
    			if(onOahu)
    				childOnOahu.sleep();
    		}
    		
    		//If First to boat, child is a passenger and waits for a rower
    		if(childrenOnBoat == 0) {
    			childrenOnBoat++;
    			//code to row to Molokai:
    			childOnOahu.wake();
    			
    			//sleeps
    			onOahu = false;
    			childOnMolokai.sleep();
    			
    			//Woken up by adult to ferry back to Oahu
    			bg.ChildRowToOahu();
    			boatIsOnOahu = true;
    			//more code
    			
    			childrenOnBoat = 0;
    			//more code
    			childOnOahu.sleep();
    			
    		}
    		
    		//If second to boat, child is a rower
    		else if(childrenOnBoat == 1) {
    			//two children always bring the boat back from Oahu and check
    			//if they are done before returning to Oahu for an adult
    			childrenOnBoat++;
    			bg.ChildRowToMolokai();
    			bg.ChildRideToMolokai();
    			boatIsOnOahu = false;
    			
    			//code
    			
    			//check if done
    			//NEED TO CREATE TRACKER FOR NUMBER OF RIDERS PASSED TO MOLOKAI
    			if(initialAdults == adultsOnOahu && initialChildren == childrenOnOahu) {
    				//set terminal bool to false to end all loops and return
    				notDone = false;
    				return;
    			} //boat terminates after this if statement
    			
    			//else not done, so send one back to Oahu
    			else {
    				bg.ChildRowToOahu();
    				//code for waking
    				
    				childrenOnBoat = 0;
    				boatIsOnOahu = true;
    				
    				//code for sleep
    				childOnOahu.sleep();
    			}
    			
    		}
    		
    	}
    }

    static void SampleItinerary()
    {
	// Please note that this isn't a valid solution (you can't fit
	// all of them on the boat). Please also note that you may not
	// have a single thread calculate a solution and then just play
	// it back at the autograder -- you will be caught.
	System.out.println("\n ***Everyone piles on the boat and goes to Molokai***");
	bg.AdultRowToMolokai();
	bg.ChildRideToMolokai();
	bg.AdultRideToMolokai();
	bg.ChildRideToMolokai();
    }
    
}
