package vues;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import control.Controleur;

@SuppressWarnings("serial")
public class StartMenu extends VueMotus {
	
	public StartMenu(Controleur control) {
		super("Motus",new ArrayList<String>(Arrays.asList("jouer","scores et statistiques")),control);
		addNewListeners();
		sing();
	}
	
	private void addNewListeners() {
		boutons.get(0).addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					setVisible(false);
					new PlaySelectionMenu(controleur);
				}
			}
		});
		
		boutons.get(0).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				new PlaySelectionMenu(controleur);
			}
		});
		
	}
	
	public void sing() {
		voix.playWav("../ressources/intro_motus.wav");
	}
				

}
