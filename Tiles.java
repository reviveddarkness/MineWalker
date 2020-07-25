import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**The purpose of this class is to construct and manipulate all all the individual tile buttons. All methods pertaining
 * to the buttons are here.
 * 
 * @author Jared
 *
 */
public class Tiles 
{
	private ImageIcon[] tileStates; //an array of all the textures
	private JButton tile; //the tile itself
	private int tileStatus; //tells the tile which status and texture to choose from array
	private boolean hasBomb; //tells whether or not a tile has a bomb
	private boolean hasExploded; //tells whether or not a tile has exploded
	private boolean safePath; //tells whether or not a tile is safe
	private int xCoordinate, yCoordinate; //keeps track of the pressed button's current position
	private static int pathx, pathy; //keeps track of person's position
	private static Tiles[][] reference; //gets reference to all the other buttons
	private ArrayList<Point> safePathReference; //gets reference to the safe path
	private static JLabel scrLabel, lvsLabel; //gives reference to the Labels on the layout so this class can live change the score and lives counters on screen.
	private static int scr, lvs; //values for the score and lives
	private static  boolean timerSwap; //tells the timer to swap between the two images.
	private static Timer animationTimer; //tells the animation to swap back.
	private static JPanel gmBd; //reference to the gameboard Panel.
	private static JButton bombButton, pathButton; //reference to the GUI buttons.
	private static int tempx, tempy;
	private static ArrayList<Point> pathTaken;
	private static boolean didWin;
	
	/**This is the constructor for the buttons. The buttons hold an enormous amount of states and variables to account
	 * for all the different functionality in the program. As such it relies purely on this to initialize all of them at
	 * startup.
	 * 
	 * *The tileStates array holds all the different images for different tile situations.
	 * *The tile is simply the Jbutton that appears on screen.
	 * *The tileStatus acts as an index variable to help determine the right state for the tile.
	 * *The hasBomb is a variable that gets initialized as true if and only if the tile does not lie on the safe path,
	 * it does what the name implies and indicates Whether or not a tile has a bomb. This variable is randomly assigned
	 * true or false to make the bomb placement random.
	 * *The hasExploded variable does as the name implies and is only set to true if you step on a bomb, die, and the space
	 * has exploded.
	 * *The safePath variable is a boolean that indicates whether this tile lies on the safe path. This is determined
	 * by calling to the RandomWalk class in the MineWalkerPanel class, passing that value here, and comparing all the
	 * values in the ArrayList from there to the ones here. The ones that match in x and y coordinates are on the safe
	 * path and are therefore deemed true in this regard.
	 * *xCoordinate and yCoordinate indicate the physical grid placement of the JButton.
	 * *pathx and pathy indicate the player's current coordinates on the grid.
	 * *The reference array gives reference for the other buttons and is passed from MineWalkerPanel. This allows the
	 * other buttons to be manipulated if need be even if you are currently in the instance of a different button.
	 * *The safePathReference is the ArrayList that comes from RandomWalk, it contains all the coordinates of the safe
	 * spaces along the safe path for comparison
	 * *The scrLabel and lvsLabel values contain the reference to the Labels on the northPanel so it can live change them
	 * when the values change.
	 * The scr and lvs values pertain to the actual values of score and lives used to set the Labels.
	 * The timerswap boolean is used to tell the timer to alternate between the two frames to produce the flashing
	 * animation.
	 * The gmBd gives reference to the gameBoard and is exclusively used to tie the key bindings to.
	 * The bombButton and pathButton JButtons give reference to the GUI elements on the south Panel bombButton and pathButton.
	 * I passed these along so that I could change their tags when their clicked. (ex. "Show Bombs", "Hide Bombs")
	 * 
	 * @param y
	 * @param x
	 * @param width
	 * @param height
	 * @param path
	 * @param score
	 * @param lives
	 */
	public Tiles(int y, int x, int width, int height, ArrayList<Point> path, JLabel score, JLabel lives, JPanel gameboard, JButton bButton, JButton pButton)
	{
		didWin = false;
		pathTaken = new ArrayList<Point>();
		bombButton = bButton;
		pathButton = pButton;
		gmBd = gameboard;
		gmBd.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "move left");
		gmBd.getActionMap().put("move left", new movingWithKeys(1));
		gmBd.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "move right");
		gmBd.getActionMap().put("move right", new movingWithKeys(2));
		gmBd.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "move up");
		gmBd.getActionMap().put("move up", new movingWithKeys(3));
		gmBd.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "move down");
		gmBd.getActionMap().put("move down", new movingWithKeys(4));
		timerSwap = false;
		lvs = 5;
		scr = 1000;
		scrLabel = score;
		lvsLabel = lives;
		safePath = false;
		safePathReference = path;
		Random rng = new Random(); //for bombs
		int genNum = rng.nextInt(4);
		xCoordinate = x;
		yCoordinate = y;
		convertToTiles();
		pathx = 0;
		pathy = width - 1;
		reference = new Tiles[width][height];
		if(safePath == false)
		{
			if(genNum == 0)
			{
				hasBomb = true;
			}
			else
			{
				hasBomb = false;
			}
		}
		hasExploded = false;
		tileStatus = 0;
		tile = new JButton();
		tile.addActionListener(new ButtonListener());
		tile.setPreferredSize(new Dimension(80, 80));
		tile.setOpaque(false);
		tile.setContentAreaFilled(false);
		tile.setFocusPainted(false);
		tile.setBorderPainted(false);
		
		tileStates = new ImageIcon[11];
		tileStates[0] = new ImageIcon("GrassyField.png");
		tileStates[1] = new ImageIcon("Normal.png"); //Normal
		tileStates[2] = new ImageIcon("MildlyAfraid.png");
		tileStates[3] = new ImageIcon("Scared.png");
		tileStates[4] = new ImageIcon("Bomb.png");
		tileStates[5] = new ImageIcon("Explode.png");
		tileStates[6] = new ImageIcon("Sign.png");
		tileStates[7] = new ImageIcon("NormalAnimated.png");
		tileStates[8] = new ImageIcon("MildlyAfraidAnimated.png");
		tileStates[9] = new ImageIcon("ScaredAnimated.png");
		tileStates[10] = new ImageIcon("Footstep.png");
		
		tile.setIcon(tileStates[tileStatus]);
	}
	
	/**This method takes the safePathReference ArrayList from RandomWalk and compares it to our grid so that a safe path
	 * to the end is always guaranteed.
	 * 
	 */
	public void convertToTiles()
	{
		for(int inc = 0; inc < safePathReference.size(); inc++)
		{
			int x = (int) safePathReference.get(inc).getX();
			int y = (int) safePathReference.get(inc).getY();
			if(xCoordinate == x && yCoordinate == y)
			{
				safePath = true;
			}
		}
	}
	
	public void changeDifficulty(int difficulty)
	{
		Random rng = new Random();
		for(int inc = 0; inc < reference.length; inc++)
		{
			for(int inc2 = 0; inc2 < reference[0].length; inc2++)
			{
				int random = 0;
				if(difficulty != 0)
				{
					random = rng.nextInt((int) Math.ceil(10/difficulty));
				}
				else
				{
					random = rng.nextInt(10);
				}
				reference[inc2][inc].hasBomb = false;
				if(reference[inc2][inc].safePath == false)
				{
					if(random == 0)
					{
						reference[inc2][inc].hasBomb = true;
					}
				}
			}
		}
	}
	
	/**Returns the x value of the character's location.
	 * 
	 * @return pathx
	 */
	public int getPathx()
	{
		return pathx;
	}
	
	/**Returns the y value of the character's location.
	 * 
	 * @return pathy
	 */
	public int getPathy()
	{
		return pathy;
	}
	
	/**Called to from the MineWalkerPanel, sets up the reference for the button array.
	 * 
	 * @param tile
	 */
	public void setReference(Tiles[][] tile)
	{
		reference = tile;
	}
	
	/**Gives reference to the button for the MineWalkerPanel class when it's building the game Panel.
	 * 
	 * @return
	 */
	public JButton getButton()
	{
		return tile;
	}
	
	/**If a space with a bomb is landed on, this is triggered. It sets the tile image to the explode image, removes a life,
	 * and resets the character back to the starting position.
	 * 
	 */
	public void setExplode()
	{
		hasExploded = true;
		
		try {
			InputStream in = new FileInputStream("Bomb.wav");
			AudioStream audioStream = new AudioStream(in);
			AudioPlayer.player.start(audioStream);
		} 
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Bomb.wav not found.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		if(scr >= 100)
		{
			scr -= 100;
		}
		scrLabel.setText("Score: " + scr);
		lvs -= 1;
		lvsLabel.setText("Lives: " + lvs);
		tileStatus = 5;
		tile.setIcon(tileStates[tileStatus]);
		animationTimer.stop();
		reference[pathy][pathx].didLose();
	    
		for(int inc = 0; inc < reference.length; inc++)
		{
			for(int inc2 = 0; inc2 < reference[0].length; inc2++)
				{
				resetAfterExplosion();
				}
		}
		hasBomb = false;
	}
	
	/**Checks to see if you lost.
	 * 
	 */
	public void didLose()
	{
		if(lvs == 0)
		{
			JOptionPane.showMessageDialog(null, "You Lose!");
			reset();
		}
	}
	
	/**This method does as the name implies and checks if the person won after every movement. If they did it displays a
	 * JOptionPane saying so and completes the rest of the actions associated with winning.
	 * 
	 */
	public void didWin()
	{
		if(pathy == 0 && pathx == reference[0].length - 1)
		{
			try {
				InputStream in = new FileInputStream("VF.wav");
				AudioStream audioStream = new AudioStream(in);
				AudioPlayer.player.start(audioStream);
			} 
			catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "VF.wav not found.");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "You win!\n Your final score was: " + scr + ".");
			for(int inc = 0; inc < reference.length; inc++)
			{
				for(int inc2 = 0; inc2 < reference[0].length; inc2++)
				{
					reference[inc2][inc].showBombs();
				}
			}
			for(int inc = 0; inc < pathTaken.size(); inc++)
			{
				for(int inc1 = 0; inc1 < reference.length; inc1++)
				{
					for(int inc2 = 0; inc2 < reference[0].length; inc2++)
					{
						if(pathTaken.get(inc).getX() == reference[inc2][inc1].xCoordinate && pathTaken.get(inc).getY() == reference[inc2][inc1].yCoordinate)
						{
							reference[inc2][inc1].tile.setIcon(tileStates[10]);
						}
					}
				}
			}
			didWin = true;
		}
	}
	
//	public void leaderBoard()
//	{
//	}
	
	/**This method returns whether or not a space has a bomb.
	 * 
	 * @return hasBomb
	 */
	public boolean hasBomb()
	{
		return hasBomb;
	}
	
	/**This method is called by the MineWalkerPanel class, and as said there, sets the tiles that are safe to have the
	 * texture reflecting so.
	 * 
	 */
	public void showPath()
	{
		pathButton.setText("Hide Path");
		if(safePath == true)
		{
			tileStatus = 6;
			tile.setIcon(tileStates[tileStatus]);
		}
	}
	
	/**Does the same thing as the showPath method except with bombs.
	 * 
	 */
	public void showBombs()
	{
		bombButton.setText("Hide Bombs");
		if(hasBomb == true)
		{
			tileStatus = 4;
			tile.setIcon(tileStates[tileStatus]);
		}
	}
	
	/**Resets the board tiles after showing bombs. After clicking the show bombs button a second time.
	 * 
	 */
	public void resetAfterShowBombs()
	{
		bombButton.setText("Show Bombs");
		tileStatus = 0;
		tile.setIcon(tileStates[tileStatus]);
		reference[pathy][pathx].getButton().setIcon(tileStates[1]);
	}
	
	/**Resets the board tiles after showing signs indicating the path. After clicking the show path button a second time.
	 * 
	 */
	public void resetAfterShowPath()
	{
		pathButton.setText("Show Path");
		tileStatus = 0;
		tile.setIcon(tileStates[tileStatus]);
		reference[pathy][pathx].getButton().setIcon(tileStates[1]);
	}
	
	/**A special reset function pertaining purely to after an explosion.
	 * 
	 */
	public void resetAfterExplosion()
	{
		if(hasExploded == false)
		{
			tileStatus = 0;
			tile.setIcon(tileStates[tileStatus]);
		}
		reference[tempy][tempx].setScared();
		pathy = tempy;
		pathx = tempx;
		
	}
	
	/**Resets the board altogether for a new game.
	 * 
	 */
	public void reset()
	{
		
		tileStatus = 0;
		tile.setIcon(tileStates[tileStatus]);
		pathx = 0;
		pathy = reference.length - 1;
		lvs = 5;
		scr = 1000;
		didWin = false;
		tempx = pathx;
		tempy = pathy;
		lvsLabel.setText("Lives: " + lvs);
		scrLabel.setText("Score: " + scr);
		bombButton.setText("Show Bombs");
		pathButton.setText("Show Path");
		pathTaken = new ArrayList<Point>();
		for(int inc = 0; inc < reference.length; inc++)
		{
			for(int inc2 = 0; inc2 < reference[0].length; inc2++)
			{
				reference[inc2][inc].hasExploded = false;
				reference[inc2][inc].resetAfterExplosion();
			}
		}
	}
	
	/**Checks how many spaces in the 8 spaces around the character have bombs, and then displays the appropriate texture
	 * for 1, 2, or 3 bombs. It also has logic for if you are in a corner, along one of the sides, or just in the center.
	 * 
	 */
	public void setScared()
	{
		int bombsNearMe = 0;
		if(animationTimer != null)
		{
			animationTimer.stop();
		}
		/**Checks for bombs if you are anywhere that isn't one of the sides of the screen.
		 * 
		 */
		if(pathx - 1 > -1 && pathy + 1 < reference[0].length && pathx + 1 < reference.length && pathy - 1 > 0)
		{
			for(int inc = pathx - 1; inc <= pathx + 1; inc++)
			{
				for(int inc2 = pathy + 1; inc2 >= pathy - 1; inc2--)
				{
					if(reference[inc2][inc].hasBomb())
					{
						bombsNearMe += 1;
					}
				}
			}
		}
		/**Checks for bombs if and only if you are along the bottom of the screen.
		 * 
		 */
		if(pathx - 1 > -1 && pathx + 1 < reference.length && pathy + 1 == reference[0].length)
		{
			for(int inc = pathx - 1; inc <= pathx + 1; inc++)
			{
				for(int inc2 = pathy; inc2 >= pathy - 1; inc2--)
				{
					if(reference[inc2][inc].hasBomb())
					{
						bombsNearMe += 1;
					}
				}
			}
		}
		/**Checks for bombs if and only if you are along the right side of the screen.
		 * 
		 */
		if(pathx - 1 == -1 && pathy + 1 < reference[0].length && pathy - 1 > -1)
		{
			for(int inc = pathx; inc <= pathx + 1; inc++)
			{
				for(int inc2 = pathy + 1; inc2 >= pathy - 1; inc2--)
				{
					if(reference[inc2][inc].hasBomb())
					{
						bombsNearMe += 1;
					}
				}
			}
		}
		/**Checks for bombs if and only if you are along the top of the Screen.
		 * 
		 */
		if(pathx - 1 > -1 && pathx + 1 < reference.length && pathy - 1 == -1)
		{
			for(int inc = pathx - 1; inc <= pathx + 1; inc++)
			{
				for(int inc2 = pathy; inc2 <= pathy + 1; inc2++)
				{
					if(reference[inc2][inc].hasBomb())
					{
						bombsNearMe += 1;
					}
				}
			}
		}
		/**Checks for bombs if and only if you are along the left side of the screen.
		 * 
		 */
		if(pathx + 1 == reference.length && pathy + 1 < reference[0].length && pathy - 1 > -1)
		{
			for(int inc = pathx; inc <= pathx - 1; inc--)
			{
				for(int inc2 = pathy + 1; inc2 >= pathy - 1; inc2--)
				{
					if(reference[inc2][inc].hasBomb())
					{
						bombsNearMe += 1;
					}
				}
			}
		}
		/**Checks for bombs if and only if you are in the upper left hand corner.
		 * 
		 */
		if(pathx - 1 == -1 && pathy - 1 == -1)
		{
			for(int inc = pathx; inc <= pathx + 1; inc++)
			{
				for(int inc2 = pathy; inc2 <= pathy + 1; inc2++)
				{
					if(reference[inc2][inc].hasBomb())
					{
						bombsNearMe += 1;
					}
				}
			}
		}
		/**Checks for bombs if and only if you are in the lower right hand corner.
		 * 
		 */
		if(pathx + 1 == reference.length && pathy + 1 == reference[0].length)
		{
			for(int inc = pathx; inc >= pathx - 1; inc--)
			{
				for(int inc2 = pathy; inc2 >= pathy - 1; inc2--)
				{
					if(reference[inc2][inc].hasBomb())
					{
						bombsNearMe += 1;
					}
				}
			}
		}
		if(bombsNearMe <= 1)
		{
			tileStatus = 1;
			tile.setIcon(tileStates[tileStatus]);
			animationTimer = new Timer(1000, new TimerActionListener());
    	    animationTimer.start();
		}
		if(bombsNearMe == 2)
		{
			tileStatus = 2;
			tile.setIcon(tileStates[tileStatus]);
    	    animationTimer = new Timer(1000, new TimerActionListener2());
    	    animationTimer.start();
		}
		if(bombsNearMe >= 3)
		{
			tileStatus = 3;
			tile.setIcon(tileStates[tileStatus]);
			animationTimer = new Timer(1000, new TimerActionListener3());
    	    animationTimer.start();
		}
	}
	
	/**Checks to see if the square you are clicking on is within one step. If it is not the it returns false and it will
	 * not allow you to move there. This prevents jumping or diagonal movement.
	 * 
	 * @return canWalk
	 */
	public boolean canWalkHere()
	{
		boolean canWalk = false;
		if(Math.abs(xCoordinate - pathx) <= 1 && Math.abs(yCoordinate - pathy) <= 1)
		{
			if(Math.abs(xCoordinate - pathx) != Math.abs(yCoordinate - pathy))
			{
				canWalk = true;
			}
		}
		return canWalk;
	}
	
	/**This class using swing's keybinding system to allow keyboard controls! Depending on which key is pressed a direction
	 * number is passed to indicate the proper function for that direction.
	 * 
	 * Most of the code for action operation is a slightly modified version of my clicking tile logic.
	 * 
	 * @author Jared
	 *
	 */
	@SuppressWarnings("serial")
	public class movingWithKeys extends AbstractAction
	{
		int direction;
		public movingWithKeys(int direction)
		{
			this.direction = direction;
		}
		public void actionPerformed(ActionEvent arg0) 
		{
			if(direction == 1 && pathx - 1 != -1)
		    {
				if (reference[pathy][pathx - 1].hasExploded != true && didWin != true)
				{
					try {
						InputStream in = new FileInputStream("Walking.wav");
						AudioStream audioStream = new AudioStream(in);
						AudioPlayer.player.start(audioStream);
					} 
					catch (FileNotFoundException el) {
						JOptionPane.showMessageDialog(null, "Walking.wav not found.");
					}
					catch (IOException el) {
						el.printStackTrace();
					}
			
					if(scr >= 10)
					{	
						scr -= 10;
					}
					tempx = pathx;
					tempy = pathy;
					scrLabel.setText("Score: " + scr);
					reference[pathy][pathx].getButton().setIcon(tileStates[0]);
					pathx -= 1; //updates the new coordinates of the character.
					reference[pathy][pathx].setScared(); //sets the proper emotion based upon amount of bombs near him.
					if(reference[pathy][pathx].hasBomb())
					{
						reference[pathy][pathx].setExplode();
					}
					else
					{
						pathTaken.add(new Point(pathx, pathy));
						reference[pathy][pathx].didWin();
					}
				}
			}
		
			if(direction == 2 && pathx + 1 != reference.length)
		    {
			    if(reference[pathy][pathx + 1].hasExploded != true && didWin != true) 
			    {
			    	try {
						InputStream in = new FileInputStream("Walking.wav");
						AudioStream audioStream = new AudioStream(in);
						AudioPlayer.player.start(audioStream);
					} 
					catch (FileNotFoundException el) {
						JOptionPane.showMessageDialog(null, "Walking.wav not found.");
					}
					catch (IOException el) {
						el.printStackTrace();
					}
					
					if(scr >= 10)
					{
						scr -= 10;
					}
					tempx = pathx;
					tempy = pathy;
					scrLabel.setText("Score: " + scr);
					reference[pathy][pathx].getButton().setIcon(tileStates[0]);
					pathx += 1;
					reference[pathy][pathx].setScared(); //sets the proper emotion based upon amount of bombs near him.
					if(reference[pathy][pathx].hasBomb())
					{
						reference[pathy][pathx].setExplode();
					}
					else
					{
						pathTaken.add(new Point(pathx, pathy));
						reference[pathy][pathx].didWin();
					}
			    }
			}
		    
		    if(direction == 3 && pathy - 1 != -1)
		    {
			    if(reference[pathy - 1][pathx].hasExploded != true && didWin != true) 
			    {
			    	try {
						InputStream in = new FileInputStream("Walking.wav");
						AudioStream audioStream = new AudioStream(in);
						AudioPlayer.player.start(audioStream);
					} 
					catch (FileNotFoundException el) {
						JOptionPane.showMessageDialog(null, "Walking.wav not found.");
					}
					catch (IOException el) {
						el.printStackTrace();
					}
					
					if(scr >= 10)
					{
						scr -= 10;
					}
					tempx = pathx;
					tempy = pathy;
					scrLabel.setText("Score: " + scr);
					reference[pathy][pathx].getButton().setIcon(tileStates[0]);
					pathy -= 1;
					reference[pathy][pathx].setScared(); //sets the proper emotion based upon amount of bombs near him.
					if(reference[pathy][pathx].hasBomb())
					{
						reference[pathy][pathx].setExplode();
					}
					else
					{
						pathTaken.add(new Point(pathx, pathy));
						reference[pathy][pathx].didWin();
					}
			    }
		    }
		    
		    if(direction == 4 && pathy + 1 != reference[0].length)
		    {
			    if(reference[pathy + 1][pathx].hasExploded != true && didWin != true)
			    {
			    	try {
						InputStream in = new FileInputStream("Walking.wav");
						AudioStream audioStream = new AudioStream(in);
						AudioPlayer.player.start(audioStream);
					} 
					catch (FileNotFoundException el) {
						JOptionPane.showMessageDialog(null, "Walking.wav not found.");
					}
					catch (IOException el) {
						el.printStackTrace();
					}
					
					if(scr >= 10)
					{
						scr -= 10;
					}
					tempx = pathx;
					tempy = pathy;
					scrLabel.setText("Score: " + scr);
					reference[pathy][pathx].getButton().setIcon(tileStates[0]);
					pathy += 1;
					reference[pathy][pathx].setScared(); //sets the proper emotion based upon amount of bombs near him.
					if(reference[pathy][pathx].hasBomb())
					{
						reference[pathy][pathx].setExplode();
					}
					else
					{
						pathTaken.add(new Point(pathx, pathy));
						reference[pathy][pathx].didWin();
					}
			    }
		    }
		}
	}
	
	/**The action listener for if any button is pressed. It signals several of the other methods in this class to run
	 * so that it may execute the proper commands. For more indepth descriptions of the steps it takes, see the functions
	 * of the methods it calls above.
	 * 
	 * @author Jared
	 *
	 */
	private class ButtonListener implements ActionListener
	{

		public void actionPerformed(ActionEvent arg0) 
		{
		if(canWalkHere() == true && hasExploded != true && didWin != true)
			{
				try {
					InputStream in = new FileInputStream("Walking.wav");
					AudioStream audioStream = new AudioStream(in);
					AudioPlayer.player.start(audioStream);
				} 
				catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Walking.wav not found.");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				
				if(scr >= 10)
				{
					scr -= 10;
				}
				tempx = pathx;
				tempy = pathy;
				scrLabel.setText("Score: " + scr);
				reference[pathy][pathx].getButton().setIcon(tileStates[0]); //resets the previous tile to simulate movement.
				pathx = xCoordinate; //updates the new coordinates of the character.
				pathy = yCoordinate;
				setScared(); //sets the proper emotion based upon amount of bombs near him.
				if(hasBomb())
				{
					setExplode();
				}
				else
				{
					pathTaken.add(new Point(pathx, pathy));
					didWin();
				}
			}
		}	
	}
	
	/**Timer for the standard, non scared expression.
	 * 
	 * @author Jared
	 */
	private class TimerActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(timerSwap == true)
			{
				tileStatus = 7;
				tile.setIcon(tileStates[tileStatus]);
			}
			else
			{
				tileStatus = 1;
				tile.setIcon(tileStates[tileStatus]);
			}
			timerSwap = !timerSwap;
		}
	}
	/**Timer for the slightlyAfraid expression.
	 * 
	 * @author Jared
	 */
	private class TimerActionListener2 implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(timerSwap == true)
			{
				tileStatus = 8;
				tile.setIcon(tileStates[tileStatus]);
			}
			else
			{
				tileStatus = 2;
				tile.setIcon(tileStates[tileStatus]);
			}
			timerSwap = !timerSwap;
		}
	}
	/**Timer for the Scared expression.
	 * 
	 * @author Jared
	 *
	 */
	private class TimerActionListener3 implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(timerSwap == true)
			{
				tileStatus = 9;
				tile.setIcon(tileStates[tileStatus]);
			}
			else
			{
				tileStatus = 3;
				tile.setIcon(tileStates[tileStatus]);
			}
			timerSwap = !timerSwap;
		}
	}
}

