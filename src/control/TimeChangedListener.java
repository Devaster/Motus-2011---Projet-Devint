package control;

import java.util.EventListener;

public interface TimeChangedListener extends EventListener {

	public void update(TimeChangedEvent e);
	
}
