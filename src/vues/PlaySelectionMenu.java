package vues;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import control.Controleur;

@SuppressWarnings("serial")
public class PlaySelectionMenu extends VueMotus {

	public PlaySelectionMenu(Controleur control) {
		super("type de jeu", new ArrayList<String>(Arrays.asList("partie individuelle","partie duel","partie deux équipes")),control);
		voix.playShortText(getTitle());
		addNewListeners();
		voix.playText(getTitle());
	}

	private void addNewListeners() {
		
		boutons.get(0).addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					setVisible(false);
					new LevelDifficultyMenu(GameType.SIMPLE,controleur);
				}
			}
		});
		
		boutons.get(0).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				new LevelDifficultyMenu(GameType.SIMPLE,controleur);
			}
		});
		
		boutons.get(1).addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					setVisible(false);
					new LevelDifficultyMenu(GameType.DOUBLE,controleur);
				}
			}
		});
		
		boutons.get(1).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				new LevelDifficultyMenu(GameType.DOUBLE,controleur);
			}
		});
		
		boutons.get(2).addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					setVisible(false);
					new LevelDifficultyMenu(GameType.DOUBLE_TEAM,controleur);
				}
			}
		});
		
		boutons.get(2).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				new LevelDifficultyMenu(GameType.DOUBLE_TEAM,controleur);
			}
		});
		
		
	}
	
}