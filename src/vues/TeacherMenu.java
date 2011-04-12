package vues;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import dictionnaires.Dictionary;
import dictionnaires.Word;

@SuppressWarnings("serial")
public class TeacherMenu extends JFrame {

	private JButton chDico, addWord, addThm;
	private JPanel panel;
	private Dictionary dico;
	
	private Image img = new ImageIcon("../ressources/index.jpeg").getImage();
	
	private JLabel label1 = new JLabel(),label2 = new JLabel();
	private JButton search = new JButton("parcourir...");
	private JFileChooser chooser = new JFileChooser();
	private JTextField field = new JTextField();
	private JTextArea area = new JTextArea();
	
	private static Font ecritureBoutons = new Font("Arial",1,60);
	private static Border bordBoutons = new LineBorder(Color.WHITE);
	
	public TeacherMenu() {
		
		super("Menu des professeurs");
		
		dico = new Dictionary("../ressources/DicFra.txt");
		
		setContentPane(new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(img,0,0,null);
			}
		});
		
		setLocationRelativeTo(null);
		setResizable(false);
		
		panel = new JPanel(new GridLayout(3,1));
		panel.add(uniformiser(chDico = new JButton("Changer de dictionnaire")));
		panel.add(uniformiser(addWord = new JButton("Ajouter un mot")));
		panel.add(uniformiser(addThm = new JButton("Ajouter un thème")));
		
		add(panel,BorderLayout.CENTER);
		
		implementListeners();
		
		setSize(new Dimension(800,450));
		setVisible(true);
		
	}
	
	private JButton uniformiser(JButton b) {
		
		b.setContentAreaFilled(false);
		b.setFont(ecritureBoutons);
		b.setBorder(bordBoutons);
		
		return b;
		
	}
	
	private void implementListeners() {
		
		search.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				chooser.showOpenDialog(null);
				if(chooser.getSelectedFile()!=null) field.setText(chooser.getSelectedFile().getAbsolutePath());
				
			}
			
		});
		
		chDico.addActionListener(new ActionListener() {

			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				chDico.setEnabled(false);
				addWord.setEnabled(false);
				addThm.setEnabled(false);
				
				field.setText("");
				label1.setText("Chemin vers le nouveau dictionnaire: ");
				JOptionPane.showMessageDialog(null, new Object[] {label1,field,search});
				
				chDico.setEnabled(true);
				addWord.setEnabled(true);
				addThm.setEnabled(true);
				
			}
			
		});
		
		addWord.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				chDico.setEnabled(false);
				addWord.setEnabled(false);
				addThm.setEnabled(false);
				
				area.setPreferredSize(new Dimension(250,100));
				
				label1.setText("Rentrer le mot: ");
				label2.setText("Définition du mot (optionnel): ");
				
				field.setText("");
				area.setText("");
				
				JOptionPane.showMessageDialog(null, new Object[] {label1,field,label2,area });
				
				if(!label1.getText().isEmpty()) dico.add(new Word(field.getText(),area.getText(),null));
				
				chDico.setEnabled(true);
				addWord.setEnabled(true);
				addThm.setEnabled(true);
				
			}
			
		});
		
		addThm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				chDico.setEnabled(false);
				addWord.setEnabled(false);
				addThm.setEnabled(false);
				
				label1.setText("Rentrer le nom du thème:");
				field.setText("");
				
				JOptionPane.showMessageDialog(null, new Object[] {label1,field});
					
				chDico.setEnabled(true);
				addWord.setEnabled(true);
				addThm.setEnabled(true);
				
			}
			
		});
		
	}
	
}
