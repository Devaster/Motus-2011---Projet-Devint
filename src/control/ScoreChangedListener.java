package control;

import java.util.EventListener;

public interface ScoreChangedListener extends EventListener {

	public void update(ScoreChangedEvent e);
	
}


