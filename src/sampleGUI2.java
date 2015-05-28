import java.awt.*;

import javax.swing.*;

import java.awt.event.*;


public class sampleGUI2 {
	protected static void init(){
		JFrame frame = new JFrame("Guess that Champion!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		
		
		Font titleFont = new Font("Helvetica", Font.BOLD, 20);
		Font text = new Font ("Calibri", Font.PLAIN, 13);
		
		Container contentPane = frame.getContentPane();
		
		contentPane.setLayout(new GridBagLayout());
		
		JPanel titlePanel = new JPanel();
		titlePanel.setPreferredSize(new Dimension(800, 25));
		titlePanel.setBackground(Color.RED);
		
		JPanel content = new JPanel( new GridBagLayout());
		content.setPreferredSize(new Dimension(800, 550));
		content.setBackground(Color.GREEN);
		
		contentPane.add(titlePanel, c);
		c.gridy ++;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		contentPane.add(content, c);
		
		
		
		c.gridy = 0;
		JLabel title = new JLabel ("Guess that Champion!");
		title.setFont(titleFont);
		titlePanel.add(title, c);
		
		c.anchor = GridBagConstraints.REMAINDER;
		
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		JLabel chooseCategories = new JLabel("Please choose the categories you would like to be quizzed on: ");
		chooseCategories.setFont(text);
		c.weightx = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		content.add(chooseCategories, c);

		
//		c.gridy ++;
//		c.anchor = GridBagConstraints.LINE_START;
//		c.weightx = GridBagConstraints.REMAINDER;
//		c.weighty = 1.0;
//		
//		JCheckBox passiveQuiz = new JCheckBox ("Champion Passive");
//		content.add(passiveQuiz, c);
		
		
		frame.setLocationRelativeTo(null);
		frame.pack();
		String disc = "\"Guess That Champion!\" isn't endorsed by Riot Games and doesn't reflect \nthe views or opinions of Riot Games or anyone officially involved in producing or managing League of Legends. \nLeague of Legends and Riot Games are trademarks or registered trademarks of Riot Games, Inc. League of Legends © Riot Games, Inc.";
		JOptionPane.showMessageDialog(frame, disc, "Disclaimer", JOptionPane.INFORMATION_MESSAGE) ;
		frame.setVisible(true);
	}
	
	public static void main (String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                init();
            }
        });
	}
}
