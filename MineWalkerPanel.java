import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**The purpose of this class is to construct the grid by calling upon the tiles class and tying the buttons together.
 * It also passes along commands from the buttons in the MineWalker class to the Tiles class so they can be executed.
 * 
 * @author Jared Lytle
 *
 */
public class MineWalkerPanel 
{
	private JPanel minefield;
	private Tiles[][] tile;
	private static boolean bombsShown;
	private static boolean pathShown;
	
	/**Calls to the Tile class to build the button grid and game. It also sets the initial values of a few
	 * choice variables (bombsShown and pathShown) which come into play when the buttons to show the bombs
	 * or path are pressed.
	 * 
	 * @param width
	 * @param height
	 * @param score
	 * @param lives
	 */
	public MineWalkerPanel(int width, int height, JLabel score, JLabel lives, JButton bombButton, JButton pathButton)
	{
		bombsShown = true;
		pathShown = true;
		tile = new Tiles[width][height];
		minefield = new JPanel();
		minefield.setLayout(new GridLayout(width, height));
		RandomWalk temp = new RandomWalk(width);
		temp.step();
		ArrayList<Point> path = temp.getPath();
		for(int inc = 0; inc < width; inc++)
		{
			for(int inc2 = 0; inc2 < height; inc2++)
			{
				tile[inc][inc2] = new Tiles(inc, inc2, width, height, path, score, lives, minefield, bombButton, pathButton);
				minefield.add(tile[inc][inc2].getButton());
				if(inc2 == 0 && inc == (width - 1))
				{
					tile[inc][inc2].setScared();
				}
			}
		}
		tile[0][0].setReference(tile);
	}
	
	/**Similar to the method above this one calls to the Tiles class to manipulate various buttons. However instead
	 * of using the constructor, this method calls to the showBombs method (which shows the tiles with bombs) and the
	 * resetAfterShowBombs method which resets the board after clicking the button the second time. It tracks which
	 * instruction to do with the bombsShown boolean which was initialized when this instance was constructed.
	 * 
	 * The bombsShown boolean reverses after every run so that it knows to run the other instructions nexttime.
	 * 
	 */
	public void showBombs()
	{
		if(bombsShown == true)
		{
			for(int rows = 0; rows < tile.length; rows++)
			{
				for(int col = 0; col < tile[0].length; col++)
				{
					tile[rows][col].showBombs();
				}
			}
		}
		if(bombsShown == false)
		{
			for(int rows = 0; rows < tile.length; rows++)
			{
				for(int col = 0; col < tile[0].length; col++)
				{
					tile[rows][col].resetAfterShowBombs();
				}
			}
		}
		bombsShown = !bombsShown;
	}
	
	/**This method also calls to methods in the Tiles class. It is extremely similar to the method above, because they
	 * are based on the same idea. When the button is first pressed display all the signs, second time go back to normal
	 * and so on and so forth.
	 * 
	 * It's tracking boolean pathShown also reverses after each run so it knows what instruction set to run next time.
	 * 
	 */
	public void showPath()
	{
		if(pathShown == true)
		{
			for(int rows = 0; rows < tile.length; rows++)
			{
				for(int col = 0; col < tile[0].length; col++)
				{
					tile[rows][col].showPath();
				}
			}
		}
		if(pathShown == false)
		{
			for(int rows = 0; rows < tile.length; rows++)
			{
				for(int col = 0; col < tile[0].length; col++)
				{
					tile[rows][col].resetAfterShowPath();
				}
			}
		}
		pathShown = !pathShown;
	}
	
	
	/**This method calls to the Tiles reset method several times to reset each tile on the board for the next game.
	 * 
	 */
	public void reset()
	{
		for(int rows = 0; rows < tile.length; rows++)
		{
			for(int col = 0; col < tile[0].length; col++)
			{
				tile[rows][col].reset();
			}
		}
		bombsShown = true;
		pathShown = true;
	}
	
	/**Calls to tiles to update bombs.
	 * 
	 * @param difficulty
	 */
	public void changeDifficulty(int difficulty)
	{
		if(bombsShown == false)
		{
			for(int rows = 0; rows < tile.length; rows++)
			{
				for(int col = 0; col < tile[0].length; col++)
				{
					tile[rows][col].reset();
				}
			}
		}
		bombsShown = true;
		tile[0][0].changeDifficulty(difficulty);
	}
	
	/**Returns reference to the minefield panel so it can subsequently be integrated into the window in the MineWalker
	 * class
	 * 
	 * @return minefield
	 */
	public JPanel getJPanel()
	{
		return minefield;
	}
	
}
