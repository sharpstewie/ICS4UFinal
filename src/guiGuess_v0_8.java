/* 							GUESS THAT CHAMPION
 * 
 * ----------------------------------------------------------------------------
 * "Guess That Champion!" isn't endorsed by Riot Games and doesn't reflect 
 * the views or opinions of Riot Games or anyone 
 * officially involved in producing or managing League of Legends. 
 * League of Legends and Riot Games are trademarks or 
 * registered trademarks of Riot Games, Inc. League of Legends © Riot Games, Inc.
 * 
 * ----------------------------------------------------------------------------
 * 
 * FEATURES
 * - Fully functional gameplay w/images of champion ability/passive as hint
 * - Select which categories to be tested on in menu screen
 * - Displays score in GUI
 * - Plays sound to let user know if their guess was correct
 * 
 * NEW FEATURES
 * - Shows countdown timer
 * - Scoring becoomes disabled when timer reaches zero
 *   
 * PLANNED FEATURES
 * - Select which *champion* categories you'll be tested on (only Marksmen, only Fighters, etc.)
 * - Skip button
 * - Remove 2 options button (limited uses)
 *   
 * CODE ADJUSTMENTS
 * - 
 *   
 * KNOWN BUGS
 * - 
 * 
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.core.staticdata.Champion;

public class guiGuess_v0_8 {
	
	// Instance variables
	
	// JFrame variables
	static JFrame frame;
	static GridBagLayout gridbag;
	static GridBagConstraints c;
	
	// Champion variables
	static ArrayList<Integer> used = new ArrayList<Integer>();
	static List<Champion> champions;
	static Champion champ;
	static Font text;
	static BufferedImage champAbi;
	static BufferedImage champPics[] = new BufferedImage[4];
	static JButton champButts[] = new JButton[4];
	
	// Hint variables
	static JLabel champAbility;
	static boolean passive;
	static boolean regular;
	static boolean ultimate;
	
	// Counters/temporary variables
	static String pass;
	static int answer;
	static int i;
	
	// Keep track of score
	static JLabel scoreLabel;
	static JLabel pointsLabel;
	static int score = 0;
	static int total = 0;
	static int points = 0;
	
	// Keep track of time
	static StopWatch watch = new StopWatch();
	static Timer timer;
	static JLabel timeLabel = new JLabel();
	static long gameStart;
	static long roundStart;
	static long roundEnd;
	static long roundTime;
	static final int cap = 60;
	static long time = cap;
	
	/* Default: Passives			[X]
	 * 			Regular abilities	[X]
	 * 			Ultimate ability	[X]
	 */
	public guiGuess_v0_8() throws IOException{
		passive = true;
		regular = true;
		ultimate = true;
		getChamp();
		init();
	}
	
	/*
	 * Use parameters to select which types of icons to display
	 */
	public guiGuess_v0_8(boolean doPassives, boolean doRegulars, boolean doUltimates) throws IOException{
		passive = doPassives;
		regular = doRegulars;
		ultimate = doUltimates;
		getChamp();
		init();
	}
	
	/*
	 * First set of icons
	 */
	protected static void init() throws IOException{
		
		// Create JFrame
		frame = new JFrame("Guess That Champion!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		
		// Choose GUI Layout
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		frame.setLayout(gridbag);
		
		// Choose title of application
		JLabel title = new JLabel("Guess That Champion!");
		scoreLabel = new JLabel("Score: " + score + " / " + total);
		pointsLabel = new JLabel("Points: " + points);
		
		// Fonts
		Font titleFont = new Font("Helvetica", Font.BOLD, 25);
		text = new Font("Arial", Font.PLAIN, 13);
		title.setFont(titleFont);
		scoreLabel.setFont(text);
		pointsLabel.setFont(text);
		
		// Select champion, choose hint to be displayed
		champ = newChamp();
		getAbi();
    	
    	// Load and display image to be displayed as hint
		try{
			champAbi = ImageIO.read(new File("lib/images/abilities/" + champ.getName() + "_" + pass + ".png"));
		}catch(IOException e){
			System.out.println("lib/images/abilities/" + champ.getName() + "_" + pass + ".png");
		}
		champAbility = new JLabel(new ImageIcon(champAbi));
    	
		// Load and display correct champion image, and 3 other champions
    	answer = (int) (4 * Math.random());
    	for(int i = 0; i < champPics.length; i++){
    		if(i==answer)
    			champPics[i] = ImageIO.read(new File("lib/images/champs/" + champ.getName() + ".png"));
    		else
    			champPics[i] = ImageIO.read(new File(newChampFill()));
    		champButts[i] = new JButton(new ImageIcon(champPics[i]));
    	}
    	
    	// Display time left
    	timeLabel.setText(Long.toString(time));
		
		// Add elements to screen
		c.anchor = GridBagConstraints.NORTH;
		c.ipady = 10;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(title, c);
		frame.getContentPane().add(title);
		c.weightx = 0.0;
		gridbag.setConstraints(champAbility, c);
		frame.getContentPane().add(champAbility);
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = GridBagConstraints.RELATIVE;
		gridbag.setConstraints(champButts[0], c);
		frame.getContentPane().add(champButts[0]);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(champButts[1], c);
		frame.getContentPane().add(champButts[1]);
		
		c.gridwidth = GridBagConstraints.RELATIVE;
		gridbag.setConstraints(champButts[2], c);
		frame.getContentPane().add(champButts[2]);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(champButts[3], c);
		frame.getContentPane().add(champButts[3]);
		
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(scoreLabel, c);
		frame.getContentPane().add(scoreLabel);
		
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(pointsLabel, c);
		frame.getContentPane().add(pointsLabel);
		
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(timeLabel, c);
		frame.getContentPane().add(timeLabel);
		
		// Add listeners to buttons
		
		champButts[0].addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				try {
					handleScore(0);
					roundEnd = watch.getElapsedTimeSecs();
					roundTime = roundEnd-roundStart;
					roundStart = roundEnd;
					System.out.println("R: " + roundTime);
					if((total < champions.size() - 3) && (gameStart < 60))
						reset();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		champButts[1].addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				try {
					handleScore(1);
					roundEnd = watch.getElapsedTimeSecs();
					roundTime = roundEnd-roundStart;
					roundStart = roundEnd;
					System.out.println("R: " + roundTime);
					if((total < champions.size() - 3) && (gameStart < 60))
						reset();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		champButts[2].addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				try {
					handleScore(2);
					roundEnd = watch.getElapsedTimeSecs();
					roundTime = roundEnd-roundStart;
					roundStart = roundEnd;
					System.out.println("R: " + roundTime);
					if((total < champions.size() - 3) && (gameStart < 60))
						reset();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		champButts[3].addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				try {
					handleScore(3);
					roundEnd = watch.getElapsedTimeSecs();
					roundTime = roundEnd-roundStart;
					roundStart = roundEnd;
					System.out.println("R: " + roundTime);
					if((total < champions.size() - 3) && (gameStart < 60))
						reset();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		// Create game timer
		int delay = 1000; //milliseconds
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				gameStart = watch.getElapsedTimeSecs();
				if(gameStart < cap)
					timeLabel.setText(Long.toString(60-gameStart));
				else timeLabel.setText("0");
				timeLabel.setVisible(true);
			}
		};
		
		// Start timers
		new Timer(delay, taskPerformer).start();
		
		watch.start();
		gameStart = watch.getElapsedTimeSecs();
		roundStart = watch.getElapsedTimeSecs();
		
		// Refresh frame with new elements
		frame.setVisible(true);
	}
	
	/*
	 * Second set of icons (and onwards)
	 */
	public static void reset() throws IOException{
		
		// Select new champion, choose hint to be displayed
		champ = newChamp();
		answer = (int) (4 * Math.random());
		getAbi();
    	
    	// Load and display hint image
    	try{
    	champAbi = ImageIO.read(new File("lib/images/abilities/" + champ.getName() + "_" + pass + ".png"));
    	}catch (IOException e){System.out.println("Can't read: lib/images/abilities/" + champ.getName() + "_" + pass + ".png");}
    	frame.getContentPane().remove(champAbility);
    	champAbility = new JLabel(new ImageIcon(champAbi));
    	
    	// Load and display correct champion image, and 3 other champion images
		for(int i = 0; i < champPics.length; i++){
			if(i==answer){
				try{
				champPics[i] = ImageIO.read(new File("lib/images/champs/" + champ.getName() + ".png"));
				}catch(IOException e) {System.out.println("lib/images/champs/" + champ.getName() + ".png");}
			}else{
				try{
				champPics[i] = ImageIO.read(new File(newChampFill()));
				}catch(IOException e){ System.out.println();}
			}
		}
    	
    	for(int i = 0; i < champButts.length; i++){
    		frame.getContentPane().remove(champButts[i]);
			champButts[i] = new JButton(new ImageIcon(champPics[i]));
			champButts[i].setVisible(true);
    	}
    	
    	// Display score and time
    	frame.getContentPane().remove(timeLabel);
    	frame.getContentPane().remove(pointsLabel);
    	frame.getContentPane().remove(scoreLabel);
    	scoreLabel = new JLabel("Score: " + score + " / " + total);
    	pointsLabel = new JLabel("Points: " + points);
    	scoreLabel.setVisible(true);
    	timeLabel.setVisible(true);
    	pointsLabel.setVisible(true);
    	
    	// Add elements to screen
    	c.weightx = 0.0;
    	c.anchor = GridBagConstraints.CENTER;
    	c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(champAbility, c);
		frame.getContentPane().add(champAbility);
		
		c.gridwidth = GridBagConstraints.RELATIVE;
		gridbag.setConstraints(champButts[0], c);
		frame.getContentPane().add(champButts[0]);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(champButts[1], c);
		frame.getContentPane().add(champButts[1]);
		
		c.gridwidth = GridBagConstraints.RELATIVE;
		gridbag.setConstraints(champButts[2], c);
		frame.getContentPane().add(champButts[2]);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(champButts[3], c);
		frame.getContentPane().add(champButts[3]);
		
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(scoreLabel, c);
		frame.getContentPane().add(scoreLabel);
		
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(pointsLabel, c);
		frame.getContentPane().add(pointsLabel);
		
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(timeLabel, c);
		frame.getContentPane().add(timeLabel);
		
		// Add listeners to buttons
		
		champButts[0].addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				try {
					handleScore(0);
					roundEnd = watch.getElapsedTimeSecs();
					roundTime = roundEnd-roundStart;
					roundStart = roundEnd;
					System.out.println("R: " + roundTime);
					if((total < champions.size() - 3) && (gameStart < 60))
						reset();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		champButts[1].addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				try {
					handleScore(1);
					roundEnd = watch.getElapsedTimeSecs();
					roundTime = roundEnd-roundStart;
					roundStart = roundEnd;
					System.out.println("R: " + roundTime);
					if((total < champions.size() - 3) && (gameStart < 60))
						reset();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		champButts[2].addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				try {
					handleScore(2);
					roundEnd = watch.getElapsedTimeSecs();
					roundTime = roundEnd-roundStart;
					roundStart = roundEnd;
					System.out.println("R: " + roundTime);
					if((total < champions.size() - 3) && (gameStart < 60))
						reset();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		champButts[3].addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				try {
					handleScore(3);
					roundEnd = watch.getElapsedTimeSecs();
					roundTime = roundEnd-roundStart;
					roundStart = roundEnd;
					System.out.println("R: " + roundTime);
					if((total < champions.size() - 3) && (gameStart < 60))
						reset();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		// Refresh frame
		frame.setVisible(true);
	}
	
	/*
	 * Request list of champions from Riot API, save in array of Champions
	 */
	public static void getChamp() throws IOException{
		BufferedReader in = new BufferedReader(new FileReader("api-key.txt")); 
    	String text = in.readLine(); 
    	in.close();
    	
        RiotAPI.setMirror(Region.NA);
        RiotAPI.setRegion(Region.NA);
        RiotAPI.setAPIKey(text);
        
        champions = RiotAPI.getChampions();
	}
	
	/*
	 * Find new champ, mark as used
	 */
	public static Champion newChamp(){
		
		// Pull random champion from list
		int index = (int)(champions.size() * Math.random());
		
		// Make sure champion hasn't already been used as an answer
		while(used.contains(index))
			index = (int)(champions.size() * Math.random());
		
		// Save that champion
        Champion c = champions.get(index);
       
        // Add champion to used array
        used.add(index);
        
        return c;
	}
	
	/*
	 * Find new champ to fill in empty slot, don't mark as used yet
	 */
	public static String newChampFill(){
		
		// Pull random champion from list
		int index = (int)(champions.size() * Math.random());
		
		// Make sure champion hasn't already been used as an answer
		while(used.contains(index))
			index = (int)(champions.size() * Math.random());
		
		// Save that champion
		Champion c = champions.get(index);
        
        //Return the appropriate file name
        return "lib/images/champs/" + c.getName() + ".png";
	}
	
	/*
	 * Generate string for an ability type to display
	 */
	public static void getAbi(){
		String returnThis = "";
		
		if(passive){
			if(regular){
				if(ultimate){ // All enabled
					int rn = (int) (5 * Math.random());
					if(rn==0) returnThis = "Q";
					else if(rn==1) returnThis = "W";
					else if(rn==2) returnThis = "E";
					else if(rn==3) returnThis = "R";
					else returnThis = "Passive";
				}else{ // No ultimates
					int rn = (int) (4 * Math.random());
					if(rn==0) returnThis = "Q";
					else if(rn==1) returnThis = "W";
					else if(rn==2) returnThis = "E";
					else returnThis = "Passive";
				}
			}else{
				if(ultimate){ // No regular abilities
					int rn = (int) (3 * Math.random());
			    	if(rn==0) returnThis = "Q";
			    	else if(rn==1) returnThis = "R";
			    	else returnThis = "Passive";
				}else{ // Only passives
					returnThis = "Passive";
				}
			}
		}else{
			if(regular){
				if(ultimate){ // No passive
					int rn = (int) (4 * Math.random());
			    	if(rn==0) returnThis = "Q";
			    	else if(rn==1) returnThis = "W";
			    	else if(rn==2) returnThis = "E";
			    	else returnThis = "R";
				}
				else{ // Only regular abilities
					int rn = (int) (3 * Math.random());
			    	if(rn==0) returnThis = "Q";
			    	else if(rn==1) returnThis = "W";
			    	else returnThis = "E";
				}
			}else{
				if(ultimate){ // Only ultimates
					returnThis = "Passive";
				}
			}
		}
		pass = returnThis;
	}
	
	/*
	 * Play a sound
	 */
	public static void playSound(String soundFile){
		try {
			// Create AudioStream from sound file
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFile).getAbsoluteFile());
	        // Create clip from AudioStream and play clip
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Sound error on playing file: " + soundFile);
	        ex.printStackTrace();
	    }
	}
	
	/*
	 * Increment score when needed, and total guesses always
	 */
	public static void handleScore(int spot) throws IOException{
		
		if((total < champions.size() - 3) && (gameStart < 60)){
			
			int inc = (int) (400 / Math.pow(2, roundTime));
			int dec = (int) (300 / Math.pow(1.5, roundTime));
			
			// Play appropriate sound, change score
			if(answer==spot){
				playSound("lib/sounds/correct.wav");
				score++;
				points += inc;
			}else{
				playSound("lib/sounds/incorrect.wav");
				points -= dec;
			}

			total++;
//			System.out.println("P: " + points);
		}
		
		// Refresh score / points
		scoreLabel.setVisible(true);
		pointsLabel.setVisible(true);
	}
	
}