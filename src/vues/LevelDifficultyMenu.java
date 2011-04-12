package vues;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import control.Controleur;

@SuppressWarnings("serial")
public class LevelDifficultyMenu extends VueMotus {

	private GameType type;
	
	public LevelDifficultyMenu(GameType type,Controleur control) {
		super("niveau", new ArrayList<String>(Arrays.asList("facile","moyen","difficile")),control);
		addNewListeners();
		voix.playShortText(getTitle());
		this.type = type;
	}

	private void addNewListeners() {
	
			boutons.get(0).addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==KeyEvent.VK_ENTER) {
						dispose();
						controleur.launch(type,5, 10, 8);
					}
				}
			});
			
			boutons.get(0).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
					controleur.launch(type,5, 10, 8);
				}
			});
			
			boutons.get(1).addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==KeyEvent.VK_ENTER) {
						dispose();
						controleur.launch(type,8, 10, 8);
					}
				}
			});
			
			boutons.get(1).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
					controleur.launch(type,8, 10, 8);
				}
			});
			
			boutons.get(2).addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==KeyEvent.VK_ENTER) {
						dispose();
						controleur.launch(type,10, 10, 8);
					}
				}
			});
			
			boutons.get(2).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
					controleur.launch(type,10, 10, 8);
				}
			});
		
	}
	
}
