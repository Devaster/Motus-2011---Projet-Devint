package jeu;

import javax.swing.event.EventListenerList;

import control.TimeChangedEvent;
import control.TimeChangedListener;

public class HorlogeInteractive extends Horloge {

	private EventListenerList liste;
	
	public HorlogeInteractive() {
		super();
		liste = new EventListenerList();
	}
	
	public void run() {
		super.run();
		fireTimeChangedListener();
	}
	
	public void addTimeChangedListener(TimeChangedListener listener) {
		liste.add(TimeChangedListener.class, listener);
	}
	
	public void removeTimeChangedListener(TimeChangedListener listener) {
		liste.remove(TimeChangedListener.class, listener);
	}
	
	public void fireTimeChangedListener() {
		TimeChangedEvent e = new TimeChangedEvent(this,heure,min,sec);
		for(TimeChangedListener listener : liste.getListeners(TimeChangedListener.class)) listener.update(e);
	}
	
}
