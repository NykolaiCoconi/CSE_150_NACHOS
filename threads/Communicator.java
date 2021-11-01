package nachos.threads;

import java.util.Queue;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
	 private boolean wordToBeHeard =false;
     //buffer to pass word
     private int word;
     //lock for condition variables and to maintain atomicity
     private Lock lock = new Lock();
     
     //declare condition variable for listeners here
     private Condition listenerReady = new Condition(lock);
     
     //declare condition variable for speakers here
     private Condition speakerReady = new Condition(lock);
	//constructor
    public Communicator() {
    	
       
       
   
}

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
    	
    	lock.acquire();
    	
    	while(wordToBeHeard)//word to be heard
    	{	
    		// your code here
    		speakerReady.sleep();
    		
    			
    	}
    	
    	this.word = word;

		// notes that the buffer is full
		wordToBeHeard = true;
		listenerReady.wake();
		// your code here
		lock.release();
		
		
    	
    
    	
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
	//return 0;
    	
    	lock.acquire();
    	
    	listenerReady.wake();
    	while(!wordToBeHeard)
    	{
    		listenerReady.sleep();
    	}
    	
    	
    	int wordToHear = word;
    	wordToBeHeard= false;
    	speakerReady.wake();
    	//code here
    	
    	lock.release();
    	return wordToHear;
    	
    	
    	
    	
    }
    

 
    
    
}

