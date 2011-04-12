package control;

import java.util.EventListener;

public interface PlayerChangedListener extends EventListener {

	public void signalChangement(PlayerChangedEvent e);
	
}
