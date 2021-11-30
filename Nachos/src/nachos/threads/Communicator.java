package nachos.threads;

import java.util.LinkedList;

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

	public Communicator() {
		commLock = new Lock();

		listenerList= new LinkedList<Actor>();
		speakerList = new LinkedList<Actor>();
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
		commLock.acquire();
		// speak called, initialize speaker with condition and msg

		if (!listenerList.isEmpty()) {
			//speakerQueue = new Actor(speakCondition, word);
			//speakerQueue.getCond().sleep();
			Actor listen = listenerList.removeFirst();
			listen.setMsg(word);
			listen.getCond().wake();
		}
		

		// when speak is called on empty or speakers are waiting, add itself to the waiting speakers list
		// put speaker to sleep


		// when speak is called on listener waiting,
		// remove oldest actor off each list
		// wake both actors
		// set msg of speaker to msg of listener

		else {
			Actor speakActor = new Actor();
			speakActor.setMsg(word);
			speakerList.add(speakActor);
			speakActor.getCond().sleep();
		}

		//readyListener.setMsg(word);
		//listenerQueue.setMsg(word);
		commLock.release();
	}

	/**
	 * Wait for a thread to speak through this communicator, and then return
	 * the <i>word</i> that thread passed to <tt>speak()</tt>.
	 *
	 * @return	the integer transferred.
	 */   
	public int listen() {
		commLock.acquire();
		int msg = 0;

		// if no speakers available
		// when listen is called on empty or listeners are waiting, add itself to the waiting listeners list
		// after, put the actor to sleep
		if (!speakerList.isEmpty()) {
			// create listen with a new condition and no msg
			Actor readySpeaker = speakerList.removeFirst();
			msg = readySpeaker.getMsg();
			readySpeaker.getCond().wake();
		}
		
		// When listen is called when speakerList is not empty
		// remove some speaker actor off speaker list
		// wake speaker up
		// set the msg of listener to msg of speaker
		// return the msg
		// set the state to empty if the speaker list is empty
		//if (state == State.SPEAKER_WAIT) {
		else {
			Actor listenActor = new Actor();
			listenerList.add(listenActor);
			listenActor.getCond().sleep();
			msg = listenActor.getMsg();
		}

		//System.out.println("speakers left:" + speakerList.size());
		commLock.release();
		return msg; // error value. should never return here
	}

	private static Lock commLock; // lock to atomically wait for speak and listen

	private LinkedList<Actor> listenerList; // list of listener waiting for speak calls
	private LinkedList<Actor> speakerList; // list of speakers waiting for listen calls
	
	//private Actor listenerQueue;
	//private Actor speakerQueue;

	private class Actor {
		Condition2 cond;
		int msg;
		
		public Actor() {
			msg = 0;
			cond = new Condition2(commLock);
		}
		public Condition2 getCond() { //getter for cond
			return cond;
		}

		public int getMsg() { //getter for msg
			return msg;
		}

		public void setMsg(int msg) { //setter for msg
			this.msg = msg;
		}
	}
}
