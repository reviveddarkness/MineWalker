import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
/**The purpose of this program is to work in tandem with the other preprogrammed or modified classes
 * to produce a random path from the origin point (represented by the lower left hand coordinate 
 * (0, gridSize - 1)) to the endpoint (gridSize - 1, 0).
 *
 * @author Jared
 */
public class RandomWalk 
{
	Scanner keyboard = new Scanner(System.in);
	private Random rng;
	private boolean done;
	private ArrayList<Point> path;
	private Point startAndChange;
	private Point end;
	
	/**This method is used to initialize the various private variables declared above. It is the
	 * backbone of the program, as it sets up all the values for the work to be done.
	 * 
	 * @param gridSize
	 */
	public RandomWalk(int gridSize)
	{
		rng = new Random();
		done = false;
		path = new ArrayList<Point>();
		startAndChange = new Point(0, (gridSize - 1));
		end = new Point(gridSize - 1, 0);
		Point tempPoint = new Point((int) startAndChange.getX(),(int) startAndChange.getY());
		path.add(tempPoint);
	}
	
	/**This does the same thing as listed above, except it initializes the random number generator,
	 * or rng for short, with a specific seed value to generate random numbers.
	 * 
	 * @param gridSize
	 * @param seed
	 */
	public RandomWalk(int gridSize, long seed)
	{
		rng = new Random(seed);
		done = false;
		path = new ArrayList<Point>();
		startAndChange = new Point(0, (gridSize - 1));
		end = new Point(gridSize - 1, 0);
		Point tempPoint = new Point((int) startAndChange.getX(),(int) startAndChange.getY());
		path.add(tempPoint);
	}
	
	/**This method exists to do all the hard work and determination in changing the values of the
	 * program. It alone determines randomly which direction to go, when to stop incrementing, when
	 * the program has finished, and under what circumstances to increment one over the other.
	 */
	public void step()
	{
		Point breakPoint = new Point(0, 0);
		if(startAndChange.equals(breakPoint))
		{
			done = true;
		}
		while(isDone() != true)
		{
			if(path.get(path.size() - 1).getX() != end.getX() && path.get(path.size() - 1).getY() != end.getY())
			{
				//randomly generates either 0 or 1, this gives it a 50/50 chance.
				int noe = rng.nextInt(2);
				//goes north if this option is the one randomly chosen
				if(noe == 1)
				{
					startAndChange.translate(0, -1);
					Point tempPoint = new Point((int) startAndChange.getX(),(int) startAndChange.getY());
					path.add(tempPoint);
				}
				//otherwise goes east
				if(noe == 0)
				{
					startAndChange.translate(1, 0);
					Point tempPoint = new Point((int) startAndChange.getX(),(int) startAndChange.getY());
					path.add(tempPoint);
				}
			}
			else
			{
				//if it has reached the end of the x path it will only decrement y
				if(path.get(path.size()-1).getX() == end.getX() && path.get(path.size() - 1).getY() != end.getY())
				{
					startAndChange.translate(0, -1);
					Point tempPoint = new Point((int) startAndChange.getX(),(int) startAndChange.getY());
					path.add(tempPoint);
					
					//checks to see if the path is done running
					if(path.get(path.size() - 1).equals(end))
					{
						done = true;
					}
				}
				//if it has reached the end of the y path it will only increment x
				if(path.get(path.size() - 1).getY() == end.getY() && path.get(path.size() - 1).getX() != end.getX())
				{
					startAndChange.translate(1, 0);
					Point tempPoint = new Point((int) startAndChange.getX(),(int) startAndChange.getY());
					path.add(tempPoint);
					
					//checks to see if the path is done running
					if(path.get(path.size() - 1).equals(end))
					{
						done = true;
					}
				}
			}
		}
	}
	
	/**This method acts like a bridge, and is used to create the entire walk at once by calling the
	 * step() method. It literally bridges to the much heavier method above.
	 */
	public void createWalk()
	{
		step();
	}
	
	/**This just exists to tell the other components whether the heavy work has finished and the
	 * path has reached an end.
	 * 
	 * @return
	 */
	public boolean isDone()
	{
		return done;
	}
	
	/**This method exists to pass reference of the ArrayList to the
	 * toString method.
	 * 
	 * @return
	 */
	public ArrayList<Point> getPath()
	{
		return path;
	}
	
	/**This method exists to tie it all together into a nice, beautiful,
	 * well formatted string.
	 * 
	 */
	public String toString()
	{
		String result = "";
		for(int inc = 0; inc < getPath().size(); inc++)
		{
			Point stuff = getPath().get(inc);
			result += "[" + stuff.x + "," + stuff.y + "] ";
		}
		return result;
	}
}
