package nachos.threads;

import nachos.machine.*;

//Added Code
import java.util.LinkedList;
//End Added Code

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
	this.conditionLock = conditionLock;
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());

	//Added Code
	boolean intStatus = Machine.interrupt().disable();
	    
	conditionLock.release();  
		
	sleepQ.add(KThread.currentThread()); //Add thread to queue	
	KThread.currentThread().sleep();	//Sleep Current thread
	    
	conditionLock.acquire();
	    
	Machine.interrupt().restore(intStatus);
	//End Added Code
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	    
	//Added Code
	boolean intStatus = Machine.interrupt().disable();
	    
	if(!sleepQ.isEmpty()) {
		if(KThread.currentThread() != null){
			sleepQ.remove(KThread.currentThread()); //Remove from Queue
			KThread.currentThread().ready(); //Make current Thread ready
		}
	}
	    
	Machine.interrupt().restore(intStatus);
	//End Added Code
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	    
	//Added Code
	while(!sleepQ.isEmpty()) {
		wake();
	}
	//End Added Code
    }
	
    //Added Code
    public LinkedList<KThread> sleepQ = new LinkedList<KThread>();
    //End Added Code
	
    private Lock conditionLock;
}
