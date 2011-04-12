package vues;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import control.Controleur;
import control.PlayerChangedEvent;
import control.PlayerChangedListener;


@SuppressWarnings("serial")
public class DuelWindow extends GameWindow implements PlayerChangedListener {

	public DuelWindow(int nbLignes, int nbColonnes, Controleur controleur) {
		super(nbLignes,nbColonnes,controleur);
		implementNewListeners();
		signalChangement(1,11000);
	}
	
	private void implementNewListeners() {
		
		inputWordArea.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				
				if(e.getKeyCode()==KeyEvent.VK_F3) signalChangement(ctrl.askPlayer(),0);
				
			}
		});
		
	}
	
	@Override
	public void signalChangement(PlayerChangedEvent e) {
		reset();
		signalChangement(e.getNumPlayer(),Math.min(e.getDelay(), TIME_DEF));
	}
	
	private void signalChangement(int i, long delay) {
		voix.playTextWithDelay("C'est au joueur "+i+" de jouer.",delay);
	}
	
}
