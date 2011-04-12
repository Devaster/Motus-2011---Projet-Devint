package vues;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import control.Controleur;
import control.GameAnswerEvent;
import control.GameAnswerListener;
import control.ScoreChangedEvent;
import control.ScoreChangedListener;
import control.TimeChangedEvent;
import control.TimeChangedListener;

@SuppressWarnings("serial")
public class GameWindow extends JFrame implements GameAnswerListener, TimeChangedListener, ScoreChangedListener {
	
	protected static Color GOOD = Color.BLUE.darker(), MISPLACED = Color.GREEN.darker(), BAD = Color.RED.darker(), GIVEN = Color.YELLOW.darker().darker();
	private static Font fontInput = new Font("Tahoma",1,86), fontOutput = new Font("Tahoma",1,66), fontTime = new Font("Arial Black",1,50);
	private static String touches = "Appuyez sur entrée pour valider vos choix. Obtenez un bonus avec la touche majuscule. Passez au mot suivant grâce à la touche control.";
	//protected static long TIME_DEF = 5000;
	protected static long TIME_DEF = 1000;
	
	
	protected int currentLine, nbLignes, nbColonnes;
	protected MySIVOX voix;
	protected String lastProposal;
	
	protected Controleur ctrl;
	
	private JPanel left,ltop, ldown;
	protected ArrayList<ArrayList<JLabel>> answers;
	protected JTextField inputWordArea;
	private JLabel currentTime, currentScore;
	
	
	public GameWindow(int nbLignes, int nbColonnes, Controleur controleur) {
		
		super("MOTUS-Fenêtre de jeu");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		//setResizable(false);
		
		voix = new MySIVOX();
		ctrl = controleur;
		this.nbLignes = nbLignes;
		this.nbColonnes = nbColonnes;
		currentLine = 0;		
		lastProposal = "";
		
		for(int j = 0; j < nbColonnes; j++) lastProposal += "_";
		
		answers = new ArrayList<ArrayList<JLabel>>(nbLignes);
		for(int i = 0; i < nbLignes; i++) {
			answers.add(i,new ArrayList<JLabel>(nbColonnes));
		}
		
		add(getLeft());
		inputWordArea.requestFocus();
		implementListeners();
		
		setVisible(true);
		
		voix.playText("La partie va commencer ! Entrez des mots de taille "+nbColonnes+". Vous avez droit à "+nbLignes+" essais par mot. "+touches);
		
	}


	private void implementListeners() {
		
		inputWordArea.addKeyListener(new KeyAdapter() {
		
			public void keyPressed(KeyEvent e) {
                voix.stop();
				if(e.getKeyCode()==KeyEvent.VK_ESCAPE) { ctrl.retour(); dispose(); }
				if(e.getKeyCode()==KeyEvent.VK_ENTER) manageGame();
				if(e.getKeyCode()==KeyEvent.VK_CAPS_LOCK) ctrl.jocker();
				if(e.getKeyCode()==KeyEvent.VK_CONTROL) ctrl.goOn();
				if(e.getKeyCode()==KeyEvent.VK_F1) voix.playText(touches);
				if(e.getKeyCode()==KeyEvent.VK_F2) { int previous = currentLine-1; if(previous!=-1) currentLine=previous; sayResults(); currentLine = previous+1; }
			}
			
		});
		
	}


	private void manageGame() {
				
		if(inputWordArea.getText().length()==nbColonnes) {
			lastProposal = inputWordArea.getText().toUpperCase(getLocale());
			ctrl.submit(lastProposal.toLowerCase(getLocale()));
		}
		
		else voix.playText("Votre mot n'est pas de taille "+nbColonnes+".");
		
		inputWordArea.setText(null);

	}

	protected void reset() {
		
		lastProposal = "";
		
		for(int j = 0; j < nbColonnes; j++) lastProposal += "_";
		
		for(int k = 0; k < nbLignes; k++) {
			for(int i = 0; i < nbColonnes; i++) answers.get(k).get(i).setText(null);
		}
		
		currentLine=0;
		
	}
			


	private Component getLeft() {

		ltop = new JPanel(new GridLayout(nbLignes,1));
		ltop.setForeground(Color.BLACK);
	    ltop.setBackground(Color.BLACK);
	    
	    JLabel lettre;
	    JPanel aux;
	    for(int i = 0; i < nbLignes; i++) {
	    	aux = new JPanel();
	    	aux.setAlignmentX(RIGHT_ALIGNMENT);
	    	for(int j = 0; j < nbColonnes; j++) {
	    		lettre = new JLabel();
	    		lettre.setAlignmentX(CENTER_ALIGNMENT);
	    		lettre.setPreferredSize(new Dimension(fontOutput.getSize(),fontOutput.getSize()));
				lettre.setForeground(Color.BLACK);
				lettre.setFocusable(false);
				lettre.setFont(fontOutput);
				aux.add(lettre);
				answers.get(i).add(j,lettre);
	    	}
	    	aux.setBorder(new LineBorder(Color.black,4));
	    	ltop.add(aux);
	    }
	    
	    inputWordArea = new JTextField();
		inputWordArea.setHorizontalAlignment(JTextField.CENTER);
		inputWordArea.setForeground(Color.BLACK);
		inputWordArea.setBackground(Color.WHITE);
		inputWordArea.setFont(fontInput);
		inputWordArea.setBorder(new LineBorder(Color.black,5));
		
		currentTime = new JLabel("   Temps : 00:00:00   ");
		currentTime.setForeground(Color.BLACK);
		currentTime.setBackground(Color.WHITE);
		currentTime.setAlignmentX(CENTER_ALIGNMENT);
		currentTime.setFont(fontTime);
		
		currentScore = new JLabel("   Score : 0000   ");
		currentScore.setForeground(Color.BLACK);
		currentScore.setBackground(Color.WHITE);
		currentScore.setAlignmentX(CENTER_ALIGNMENT);
		currentScore.setFont(fontTime);
		
		ldown = new JPanel();
		ldown.setLayout(new BoxLayout(ldown,BoxLayout.LINE_AXIS));
		ldown.add(currentTime);
		ldown.add(currentScore);
		
		left = new JPanel();
		left.setLayout(new BoxLayout(left,BoxLayout.Y_AXIS));
		left.add(ltop);left.add(inputWordArea);left.add(ldown);
	    return left;
		
	}


	@Override
	public void dealWith(GameAnswerEvent e) {
		
		switch(e.getCause()) {
		case CMP_PROPOSAL:
			showResults(e.getResults(),0,nbColonnes);
			sayResults();
			currentLine++;
			break;
		case WORD_FOUND:
			//voix.playText("Bravo ! Le mot "+e.getSol()+" signifie : "+e.getDef());
			voix.playText("Bravo");
			reset();
			break;
		case END_GAME:
			inputWordArea.setEnabled(false);
			voix.playWavWithDelay("../ressources/partiEnd.wav",TIME_DEF);
			requestFocus();
			addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					voix.stop();
					if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
						ctrl.retour();
						dispose();
					}
				}
			});
			break;
		case BONUS_GIVEN:
			voix.playText("La "+((e.getPos()==0) ? "première" : (e.getPos()+1)+" ième")+" lettre est un "+goodPrononciation(e.getBonus())+".");
			lastProposal = lastProposal.substring(0, e.getPos())+e.getBonus()+lastProposal.substring(e.getPos()+1);
			lastProposal = lastProposal.toUpperCase(getLocale());
			showResults(e.getResults(),0,e.getPos());
			showResults(e.getResults(),e.getPos()+1,nbColonnes);
			answers.get(currentLine).get(e.getPos()).setText(""+lastProposal.charAt(e.getPos()));
			answers.get(currentLine).get(e.getPos()).setForeground(GIVEN);
			currentLine++;
			break;
		case NO_MORE_TRY:
			reset();
			//voix.playText("Le mot était "+e.getSol()+". "+e.getDef());
			voix.playText("Le mot était "+e.getSol());
			break;
		}
		
	}
	
	public void update(TimeChangedEvent e) {
		currentTime.setText(e.getTime());
	}
	
	protected void showResults(int[] res, int begin, int end) {
		
		for(int i = begin; i < end; i++) {
			answers.get(currentLine).get(i).setText(""+lastProposal.charAt(i));
			switch(res[i]) {
			case -1:
				answers.get(currentLine).get(i).setForeground(MISPLACED);
				break;
			case 0:
				answers.get(currentLine).get(i).setForeground(BAD);
				break;
			case 1:
				answers.get(currentLine).get(i).setForeground(GOOD);
				break;
			}
		}
	}
	
	private void sayResults() {
		String rep = "";
		for(int i = 0; i < nbColonnes; i++) {
			if(answers.get(currentLine).get(i).getForeground()==BAD) rep += " Le mot ne conti un pas la lettreu "+goodPrononciation(answers.get(currentLine).get(i).getText().charAt(0))+".";
			if(answers.get(currentLine).get(i).getForeground()==MISPLACED) rep += " La lettreu "+goodPrononciation(answers.get(currentLine).get(i).getText().charAt(0))+" n'est pas à sa place.";
			if(answers.get(currentLine).get(i).getForeground()==GOOD||answers.get(currentLine).get(i).getForeground()==GIVEN) rep += " La lettreu "+goodPrononciation(answers.get(currentLine).get(i).getText().charAt(0))+" est bien placée.";
		}
		voix.playText(rep+" Pour réentendre tappez èfe 2.");
	}
	
	private String goodPrononciation(char c) {
		switch(c) {
		case 'f':
		case 'F': return "èfe";
		case 'p':
		case 'P' : return "pé";
		case 's':
		case 'S' : return "esse";
		case 't':
		case 'T' : return "té";
		default : return ""+c;	
		}
	}


	@Override
	public void update(ScoreChangedEvent e) {
		
		currentScore.setText("   Score : "+(e.getScore()/1000)+((e.getScore()%1000)/100)+((e.getScore()%100)/10)+(e.getScore()%10)+"   ");
		
	}


	
	
	
}