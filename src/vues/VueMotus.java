package vues;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import control.Controleur;

import t2s.*;

@SuppressWarnings("serial")
public abstract class VueMotus extends JFrame {
	
	private static final Color COULEUR_FOND = Color.blue.darker().darker().darker().darker();
	private static final Color COULEUR_TEXTE = Color.white;
	private static final Border BORD_CHAMPS = new LineBorder(Color.black,5);
	private static final Font FONT_TEXTE = new Font("Georgia",1,96);
	
	private JPanel panelTitre,panelLabel,panelBoutons;
	private JLabel title;
	private JButton teach;
	protected ArrayList<JButton> boutons;
	
	protected SIVOXDevint voix;
	protected Controleur controleur;
	
	public VueMotus(String titre,ArrayList<String> buttons, Controleur control) {
		
	super(titre.toUpperCase());
	setExtendedState(MAXIMIZED_BOTH);
	setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	//setResizable(false);
	
	boutons = new ArrayList<JButton>();
	
	panelTitre = new JPanel(new BorderLayout());
	
	teach = new JButton(new ImageIcon("../ressources/prof.jpg"));
	teach.setPreferredSize(new Dimension(50,10));
	teach.setToolTipText("Accès réservé aux professeurs");
	teach.setBackground(Color.white);
	
	
	panelLabel = new JPanel(new FlowLayout());
	title = new JLabel(titre.toUpperCase(getLocale()));
	uniformiser(title);
	panelLabel.add(title);
	uniformiser(panelLabel);
	
	panelTitre.add(teach,BorderLayout.EAST);
	panelTitre.add(panelLabel,BorderLayout.CENTER);
	add(panelTitre,BorderLayout.NORTH);
	
	buttons.add("QUITTER");
	panelBoutons = new JPanel(new GridLayout(buttons.size(),1));
	
	for(String button : buttons) {
		final JButton bouton = new JButton(button.toUpperCase(getLocale()));
		boutons.add(bouton);
		uniformiser(bouton);
		bouton.setBackground(COULEUR_FOND);
		bouton.setForeground(COULEUR_TEXTE);
		
		panelBoutons.add(bouton);
	}
	
	add(panelBoutons,BorderLayout.CENTER);
	
	setVisible(true);
	voix = new SIVOXDevint();
	controleur = control;
	implementListeners();
	requestFocus();
	
	}
	
	private void uniformiser(JComponent cmp) {
		cmp.setBorder(BORD_CHAMPS);
		cmp.setFont(FONT_TEXTE);
	}
	
	private void implementListeners() {
		
		for(JButton bouton : boutons) {
			final JButton button = bouton;
			bouton.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					voix.stop();
					if(e.getKeyCode()==KeyEvent.VK_UP) previous(button).requestFocus();
					if(e.getKeyCode()==KeyEvent.VK_DOWN) next(button).requestFocus();
					if(e.getKeyCode()==KeyEvent.VK_ESCAPE) dispose();
				}
			});
			
			bouton.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					voix.playShortText(button.getText());
				}
			});
			
			bouton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					voix.stop();
				}
			});
		}
		
		int n = boutons.size()-1; //c'est le dernier à  priori mais bon...
		boutons.get(n).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		boutons.get(n).addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) dispose();
			}
		});
		
		teach.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				new TeacherMenu();
				boutons.get(0).requestFocus();
				
			}
			
		});
		
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				voix.stop();
				switch(e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE: dispose();
					break;
				case KeyEvent.VK_UP:
				case KeyEvent.VK_DOWN:
					boutons.get(0).requestFocus();
					break;
				}
			}
		});
		
	}

	private JButton previous(JButton button) {
		return boutons.get((boutons.size()+boutons.indexOf(button)-1)%boutons.size());
	}
	
	private JButton next(JButton button) {
		return boutons.get((boutons.indexOf(button)+1)%boutons.size());
	}
	

}
