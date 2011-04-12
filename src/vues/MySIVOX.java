package vues;

import java.util.Timer;
import java.util.TimerTask;

import t2s.SIVOXDevint;

public class MySIVOX extends SIVOXDevint {

	private Timer timer;
	
	public MySIVOX() {
		super();
		timer = new Timer();
	}
	
	public void playShortTextWithDelay(final String text, long delay) {
		timer.schedule(new TimerTask() {
			public void run() {
				playShortText(text);
			}
		}, delay);
	}
	
	public void playTextWithDelay(final String text, long delay) {
		timer.schedule(new TimerTask() {
			public void run() {
				playText(text);
			}
		}, delay);
	}
	
	public void playWavWithDelay(final String file, long delay) {
		timer.schedule(new TimerTask() {
			public void run() {
				playWav(file);
			}
		}, delay);
	}
	
	public void stop() {
		super.stop();
		timer.cancel();
		timer = new Timer();
	}
	
}
