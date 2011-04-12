package control;

import java.util.EventObject;

@SuppressWarnings("serial")
public class TimeChangedEvent extends EventObject {

	private String time;
	
	public TimeChangedEvent(Object source, int heure, int min, int sec) {
		super(source);
		time = "   Temps : "+( (heure<10) ? "0"+heure : heure )+":"+( (min<10) ? "0"+min : min  )+":"+( (sec<10) ? "0"+sec : sec  )+"   ";
	}
	
	public String getTime() {
		return time;
	}
	
}
