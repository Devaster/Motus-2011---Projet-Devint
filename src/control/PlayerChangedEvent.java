package control;

import java.util.EventObject;

@SuppressWarnings("serial")
public class PlayerChangedEvent extends EventObject {

	private int numPlayer;
	private long delay;
	private boolean reset;

	public PlayerChangedEvent(Object source, int n, long delay,boolean b) {
		super(source);
		numPlayer = n;
		this.delay = delay;
		reset = b;
	}
	
	public int getNumPlayer() {
		return numPlayer;
	}
	
	public long getDelay() {
		return delay;
	}
	
	public boolean needToReset() {
		return reset;
	}
	
}
