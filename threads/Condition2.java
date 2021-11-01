package nachos.threads;

import nachos.machine.*;

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

	conditionLock.release();  
	    
	//Added Code
	boolean intStatus = Machine.interrupt().disable();
		
	sleepQ.waitForAccess(currentThread());	
	KThread.currentThread().sleep();	//Sleep Current thread
		
	Machine.interrupt().restore(intStatus);
	//End Added Code
	    
	conditionLock.acquire();
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
	Lib.assertTrue(conditionLock.isHeldByCurrentThread());
	    
	//Added Code	    
	boolean intStatus = Machine.interrupt().disable();
	    
	KThread temp = sleepQ.nextThread();    
	while(temp != null) {
		temp.wake();
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
    private ThreadQueue sleepQ = ThreadedKernel.scheduler.newThreadQueue(true);
    //End Added Code
	
    private Lock conditionLock;
}
