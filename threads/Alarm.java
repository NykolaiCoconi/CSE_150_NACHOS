package nachos.threads;

import java.util.TreeSet;

import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }
    
    //Create waitingThread struct
    public class WaitingThread implements Comparable<WaitingThread> {
    	public long wakeTime;
    	public KThread thread;
    	@Override public int compareTo(WaitingThread anotherThread) {
    		return Long.compare(this.wakeTime,anotherThread.wakeTime);
    	};
    };
    
    public TreeSet<WaitingThread> waitingQueue = new TreeSet<WaitingThread>();
    
    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
    	//Disable System Interrupts
    	Machine.interrupt().disable();
    	WaitingThread minThread;
    	//Check if waitingQueue is empty && if wakeTime has passed
    	while (!(waitingQueue.isEmpty()) && ((minThread = waitingQueue.first()).wakeTime <= Machine.timer().getTime())) {
    		
    		// Wake Thread
            minThread.thread.ready();
    		// Remove from waitingQueue thread
        	waitingQueue.remove(minThread);
    		
    	}
    	// Enable interrupts
    	Machine.interrupt().enable();
    	KThread.currentThread().yield();
    	
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
    	// Disable Interrupts
    	Machine.interrupt().disable();
    	System.out.println("System Time: " + Machine.timer().getTime());
    	// Validate waitTime
    	if (x > 0) {
    		// Save thread to waitingQueue
    		WaitingThread newThread = new WaitingThread();
        	newThread.wakeTime = Machine.timer().getTime() + x;
        	//System.out.println("Waketime set to:" + Machine.timer().getTime() + " + " + x + " : " + newThread.wakeTime);
        	newThread.thread = KThread.currentThread();
        	//System.out.println("Thread set to:" + newThread.thread);
        	waitingQueue.add(newThread);
        	KThread.currentThread().sleep();
    	}

    	// Enable system interrupts if invalid input
		Machine.interrupt().enable();
		//System.out.println("System Time: " + Machine.timer().getTime());
		KThread.currentThread().yield();
    }
    
    //Testing
   // Add Alarm testing code to the Alarm class
    
    public static void alarmTest1() {
    	int durations[] = {1000, 10*1000, 100*1000};
		long t0, t1;
		for (int d : durations) {
		    t0 = Machine.timer().getTime();
		    ThreadedKernel.alarm.waitUntil (d);
		    t1 = Machine.timer().getTime();
		    System.out.println ("alarmTest1: waited for " + (t1 - t0) + " ticks");
		}
    }
    public static void alarmTest2() {
    	int durations[] = {0, -1000, 100*1000};
		long t0, t1;
		for (int d : durations) {
		    t0 = Machine.timer().getTime();
		    ThreadedKernel.alarm.waitUntil (d);
		    t1 = Machine.timer().getTime();
		    System.out.println ("alarmTest2: waited for " + (t1 - t0) + " ticks");
		}
    }
    
    public static void alarmTest3() {
    	int durations[] = {60, 500, 100*1000};
		long t0, t1;
		for (int d : durations) {
		    t0 = Machine.timer().getTime();
		    ThreadedKernel.alarm.waitUntil (d);
		    t1 = Machine.timer().getTime();
		    System.out.println ("alarmTest3: waited for " + (t1 - t0) + " ticks");
		}
    }
    
    // Implement more test methods here ...
    // Invoke Alarm.selfTest() from ThreadedKernel.selfTest()
    public static void selfTest() {
    	alarmTest1();
    	// Invoke your other test methods here ...
    	alarmTest2();
    	alarmTest3();
    }
}
