package control;

import java.util.EventListener;

public interface GameAnswerListener extends EventListener {

	public void dealWith(GameAnswerEvent e);
	
}
