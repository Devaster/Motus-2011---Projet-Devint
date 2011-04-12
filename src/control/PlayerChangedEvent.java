package control;

import java.util.EventObject;

@SuppressWarnings("serial")
public class PlayerChangedEvent extends EventObject {

	private int numPlayer;
	private long delay;

	public PlayerChangedEvent(Object source, int n, long delay) {
		super(source);
		numPlayer = n;
		this.delay = delay;
	}
	
	public int getNumPlayer() {
		return numPlayer;
	}
	
	public long getDelay() {
		return delay;
	}
	
}
