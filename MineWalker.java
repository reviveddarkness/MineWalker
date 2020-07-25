import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**This class is the controller class and calls upon every other function, method, etc to build the game.
 * 
 * @author Jared Lytle
 *
 */
@SuppressWarnings("serial")
public class MineWalker extends JPanel
{
	private MineWalkerPanel gameBoard;
	private static JFrame window;
	private JPanel btmPanel;
	private JPanel eastPanel;
	private JPanel northPanel;
	private JPanel results;
	private JTextField gridSize;
	private JSlider dif;
	
	/**This method builds all the individual pieces for the final Panel and Window. That includes calling to the
	 * MineWalkerPanel class which subsequently calls to the button class to form the grid button game.
	 * 
	 * @param width
	 * @param height
	 */
	public MineWalker(int width, int height)
	{
		JButton bombButton = new JButton("Show Bombs");
		JButton showPathButton = new JButton("Show Path");
		String scr = "Score: "; //gets starting score WIP
		String life = "Lives: "; //gets starting lives
		JLabel score = new JLabel(scr + 1000);
		JLabel lives = new JLabel(life + 5);
		gameBoard = new MineWalkerPanel(width, height, score, lives, bombButton, showPathButton);
		JPanel tempPanel = new JPanel(); //
		btmPanel = new JPanel();
		btmPanel.setLayout(new BoxLayout(btmPanel, BoxLayout.X_AXIS));
		eastPanel = new JPanel();
		northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 250, 0));
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
		results = new JPanel(new BorderLayout());
		
		JButton resetButton = new JButton("Restart Current Game");
		JButton newGame = new JButton("New Game");
		gridSize = new JTextField("10");
		resetButton.addActionListener(new RestartButtonListener());
		bombButton.addActionListener(new BombListener());
		showPathButton.addActionListener(new ShowPathButtonListener());
		newGame.addActionListener(new TextListener());
		
		dif = new JSlider(JSlider.HORIZONTAL, 0, 10, 2); //Slider for difficulty
		dif.addChangeListener(new DifListener());
		dif.setBorder(BorderFactory.createTitledBorder("Difficulty"));
		dif.setMinorTickSpacing(1);
		dif.setMajorTickSpacing(2);
		dif.setPaintTicks(true);
		dif.setPaintLabels(true);
		dif.setLabelTable(dif.createStandardLabels(2));
		
		ImageIcon normal = new ImageIcon("Normal.png");
		ImageIcon mildlyAfraid = new ImageIcon("MildlyAfraid.png");
		ImageIcon scared = new ImageIcon("Scared.png");
		ImageIcon explode = new ImageIcon("Explode.png");
		ImageIcon sign = new ImageIcon("Sign.png");
		
		tempPanel.add(resetButton);
		tempPanel.add(bombButton);
		tempPanel.add(showPathButton);
		tempPanel.add(newGame);
		tempPanel.add(gridSize);
		btmPanel.add(dif);
		btmPanel.add(Box.createHorizontalGlue());
		btmPanel.add(tempPanel);
		tempPanel.setAlignmentY(CENTER_ALIGNMENT);
		
		
		JLabel imageLabel1 = new JLabel("0-1 Nearby Mines");
		imageLabel1.setFont(new Font("Arial", Font.PLAIN, 9));
		JLabel imageLabel2 = new JLabel("2 Nearby Mines");
		imageLabel2.setFont(new Font("Arial", Font.PLAIN, 9));
		JLabel imageLabel3 = new JLabel("3 Nearby Mines");
		imageLabel3.setFont(new Font("Arial", Font.PLAIN, 9));
		JLabel imageLabel4 = new JLabel("Exploded Bomb");
		imageLabel4.setFont(new Font("Arial", Font.PLAIN, 9));
		JLabel imageLabel5 = new JLabel("Pathway Sign");
		imageLabel5.setFont(new Font("Arial", Font.PLAIN, 9));
		JLabel imageHolder1 = new JLabel(normal);
		JLabel imageHolder2 = new JLabel(mildlyAfraid);
		JLabel imageHolder3 = new JLabel(scared);
		JLabel imageHolder4 = new JLabel(explode);
		JLabel imageHolder5 = new JLabel(sign);
		
		northPanel.add(score);
		northPanel.add(lives);
		
		eastPanel.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(5)));
		eastPanel.add(Box.createVerticalStrut(35));
		eastPanel.add(imageLabel1);
		eastPanel.add(imageHolder1);
		eastPanel.add(Box.createVerticalStrut(35));
		eastPanel.add(imageLabel2);
		eastPanel.add(imageHolder2);
		eastPanel.add(Box.createVerticalStrut(35));
		eastPanel.add(imageLabel3);
		eastPanel.add(imageHolder3);
		eastPanel.add(Box.createVerticalGlue());
		eastPanel.add(imageLabel4);
		eastPanel.add(imageHolder4);
		eastPanel.add(Box.createVerticalStrut(35));
		eastPanel.add(imageLabel5);
		eastPanel.add(imageHolder5);
		eastPanel.add(Box.createVerticalStrut(35));
		
		results.add(gameBoard.getJPanel(), BorderLayout.CENTER);
		results.add(btmPanel, BorderLayout.SOUTH);
		results.add(eastPanel, BorderLayout.EAST);
		results.add(northPanel, BorderLayout.NORTH);
	}
	
	/**Action Listener for the Restart Button
	 * 
	 * @author Jared
	 *
	 */
	private class RestartButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			gameBoard.reset();
		}
	}
	
	/**Action Listener for the button that shows the bombs.
	 * 
	 * @author Jared
	 *
	 */
	private class BombListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			gameBoard.showBombs();
		}
	}
	
	/**Action Listener for the button that shows the signs to indicate the safe path.
	 * 
	 * @author Jared
	 *
	 */
	private class ShowPathButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			gameBoard.showPath();
		}
	}
	
	/**Listens to the text from the box and executes the related actions when the button for the new game is pressed.
	 * 
	 * @author Jared
	 *
	 */
	private class TextListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			if(Integer.parseInt(gridSize.getText()) <= 18 && Integer.parseInt(gridSize.getText()) >= 5)
			{	
				window.dispose();
				int width = Integer.parseInt(gridSize.getText());
				int height = Integer.parseInt(gridSize.getText());
				MineWalker Panel = new MineWalker(width, height);
				window = new JFrame("Mine Walker Game");
				window.getContentPane().add(Panel.results);
				window.pack();
				window.setVisible(true);
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Your grid range is either too high or low! Please pick a number between 5 and 18");
			}
		}
	}
	
	/**This listener is triggered when you press the new game buttons, reads the data from the JTextField and generates
	 * a new game based upon that typed in grid size.
	 * 
	 * @author Jared
	 *
	 */
	private class DifListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent arg0) 
		{
			int difficulty = dif.getValue();
			gameBoard.changeDifficulty(difficulty);
		}
	}
	
	/**Main method that does surprisingly little. It just makes the window and then immediately calls for the
	 * MineWalker class to make everything else.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		ImageIcon support = new ImageIcon("Support.png");
		JOptionPane.showMessageDialog(null, "This game painstakingly supports both keyboard and mouse input.", "Welcome to MineSweeperMax!", JOptionPane.PLAIN_MESSAGE, support);
		window = new JFrame("MineSweeperMax!");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MineWalker Panel = new MineWalker(10, 10);
		window.getContentPane().add(Panel.results);
		window.pack();
		window.setVisible(true);
	}

}
