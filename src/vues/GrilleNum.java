package vues;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;



@SuppressWarnings("serial")
public class GrilleNum extends JFrame {

	private static Color VISIBLE = Color.blue, HIDDEN = Color.yellow;
	private static Font font = new Font("Times New Roman",1,100);
	private JPanel[][] cases;
	private JLabel[][] contenu;
	private HashSet<Integer> urne;

	private Random random;
	
	public GrilleNum() {
		super();
		setExtendedState(MAXIMIZED_BOTH);
		
		cases = new JPanel[5][5];
		contenu = new JLabel[5][5];
		urne = new HashSet<Integer>();
		random = new Random();
		
		setContentPane(new JPanel(new GridLayout(5,5)));
		
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				contenu[i][j] = new JLabel();
				contenu[i][j].setFont(font);
				cases[i][j] = new JPanel();
				cases[i][j].setBackground(VISIBLE);
				cases[i][j].setBorder(new LineBorder(Color.white,3));
				cases[i][j].add(contenu[i][j]);
				add(cases[i][j]);
			}
		}
		
		fillGrid();
		hideSomeCases();
		
	}
	
	private void fillGrid() {
		
		int n = 0;
		
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				while((n==0)||(urne.contains(n))) n = random.nextInt(100);
				contenu[i][j].setText(""+n);
				urne.add(n);
			}
		}
		
	}
	
	private void hideSomeCases() {
		
		int i, j;
		
		for(int k = 0; k < 8; k++) {
			
			do {
				i = random.nextInt(5);
				j = random.nextInt(5);
			}while(cases[i][j].getBackground()!=VISIBLE);
			
			contenu[i][j].setVisible(false);
			cases[i][j].setBackground(HIDDEN);
			
		}
	}
	
}
