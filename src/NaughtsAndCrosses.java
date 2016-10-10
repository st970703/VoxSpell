import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * A tic tac toe game in a separate JFrame.
 * @author/sponsor Darius Au
 * @editor Mike Lee 
 */
@SuppressWarnings("serial")
public class NaughtsAndCrosses extends JFrame {

	private NaughtsAndCrosses instance;
	private SpellingAid _sa;

	private static final int ROWS = 3;
	private static final int COLS = 3;

	private enum GameStatus { NAUGHT_WON, CROSS_WON, IN_PROGRESS, DRAW };

	private enum Turn {Naughts, Crosses};

	private Turn sideToMove = Turn.Naughts;
	private static int _naughtWins = 0;
	private static int _crossWins = 0; 
	private static int _draws = 0;

	// Values to be used for button text within a board. 
	private static String NAUGHT = "0";
	private static String CROSS = "X";
	private static String BLANK = " ";

	// A 2 dimensional array of JButton objects. Each button is intended to 
	// display the text NAUGHT, CROSS or BLANK (see above). 
	private JButton[][] _board;
	private JButton _newGame = new JButton("New Game");
	private JButton _reset = new JButton("Reset Scores");
	private JLabel _noOfNWins = new JLabel(String.valueOf(_naughtWins));
	private JLabel _noOfCWins = new JLabel(String.valueOf(_crossWins));
	private JLabel _noOfDraws = new JLabel(String.valueOf(_draws));
	
	public NaughtsAndCrosses(SpellingAid sa) {

		// set all fonts
		_noOfNWins.setFont(_noOfNWins.getFont().deriveFont(Font.BOLD));
		_noOfCWins.setFont(_noOfCWins.getFont().deriveFont(Font.BOLD));
		_noOfDraws.setFont(_noOfDraws.getFont().deriveFont(Font.BOLD));

		_newGame.setFont(_newGame.getFont().deriveFont(Font.BOLD));
		_reset.setFont(_reset.getFont().deriveFont(Font.BOLD));

		this.setTitle("TicTacToe");
		instance = this;

		_sa = sa;

		// Build the GUI.
		buildGUI();

		this.pack();
		this.setSize(400,350);
		this.setResizable(false);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				instance.dispose();

				_sa.levelCompleted();
			}
		});

		this.setLocationRelativeTo(null);
		// set up look and feel
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(this);

		this.setVisible(true);
		_sa.hideWindow();

		NaughtsAndCrosses game = this;

		AbstractAction gameAction = new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e) {
				switch(sideToMove){
				case Naughts: 
					((AbstractButton) e.getSource()).setText(NAUGHT);
					((AbstractButton) e.getSource()).setFont(new Font("Yu Gothic Medium", Font.BOLD, 60));
					sideToMove = Turn.Crosses;
					break;
				case Crosses: 
					((AbstractButton) e.getSource()).setText(CROSS);
					((AbstractButton) e.getSource()).setFont(new Font("Yu Gothic Medium", Font.BOLD, 60));
					sideToMove = Turn.Naughts;
					break;
				}
				((JButton) e.getSource()).setEnabled(false);	

				GameStatus status = game.getGameStatus();
				if (status!=GameStatus.DRAW && status!=GameStatus.IN_PROGRESS){

					for(int row = 0; row < ROWS; row++) {
						for(int col = 0; col < COLS; col++) {
							_board[row][col].setEnabled(false);
						}
					}

					if (status==GameStatus.CROSS_WON){
						
						ArrayList<String> temp = new ArrayList<String>();
						temp.add("Crosses wins??");
						FestivalSayable.sayWord(temp, 1.0, _sa.getVoice());
						_crossWins++;
						_noOfCWins.setText(String.valueOf(_crossWins));
						return;
					} else {
						ArrayList<String> temp = new ArrayList<String>();
						temp.add("Naughts wins??");
						FestivalSayable.sayWord(temp, 1.0, _sa.getVoice());
						_naughtWins++;
						_noOfNWins.setText(String.valueOf(_naughtWins));
						return;
					}

				}

				for(int row = 0; row < ROWS; row++) {
					for(int col = 0; col < COLS; col++) {
						if (_board[row][col].isEnabled()){
							return;
						}
					}
				}
				
				ArrayList<String> temp = new ArrayList<String>();
				temp.add("Draws are unacceptable??");
				FestivalSayable.sayWord(temp, 1.0, _sa.getVoice());
				
				_draws++;
				_noOfDraws.setText(String.valueOf(_draws));



			}	


		};




		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				_board[row][col].addActionListener(gameAction);
			}
		}

		_newGame.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(int row = 0; row < ROWS; row++) {
					for(int col = 0; col < COLS; col++) {
						_board[row][col].setText(BLANK);
						_board[row][col].setEnabled(true);
					}
				}
				
				ArrayList<String> temp = new ArrayList<String>();
				temp.add("new game??");
				FestivalSayable.sayWord(temp, 1.0, _sa.getVoice());

			}

		});

		_reset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(int row = 0; row < ROWS; row++) {
					for(int col = 0; col < COLS; col++) {
						_board[row][col].setText(BLANK);
						_board[row][col].setEnabled(true);
					}
				}
				_naughtWins = 0;
				_crossWins = 0;
				_draws = 0;
				_noOfNWins.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
				_noOfNWins.setText(String.valueOf(_naughtWins));
				_noOfCWins.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
				_noOfCWins.setText(String.valueOf(_crossWins));
				_noOfDraws.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));
				_noOfDraws.setText(String.valueOf(_draws));

				ArrayList<String> temp = new ArrayList<String>();
				temp.add("scores reset??");
				FestivalSayable.sayWord(temp, 1.0, _sa.getVoice());
			}

		});

	}

	private void buildGUI() {
		// Initialise the board.
		_board = new JButton[ROWS][COLS];

		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				_board[row][col] = new JButton(BLANK);
				_board[row][col].setPreferredSize(new Dimension(70,70));

			}
		}

		// TO DO: create other GUI components and lay them out as appropriate.
		// Note that _board only stores 9 JButton objects using a 3x3 array. 
		// The buttons still need to be added to the GUI - use GridLayout to 
		// add and layout the JButtons on a JPanel.  

		JPanel inputPanel = new JPanel( ); 
		JPanel newGameAndReset = new JPanel( new GridLayout(1,2));
		JPanel layout = new JPanel();
		JPanel scores = new JPanel();
		BoxLayout boxLayout = new BoxLayout(layout,BoxLayout.PAGE_AXIS);

		GridLayout ticTacToeBoard = new GridLayout(3,3);
		GridLayout scoreBoard = new GridLayout(2,3);

		inputPanel.setLayout(ticTacToeBoard);

		// All icons from https://www.iconfinder.com/
		_newGame.setBackground(Color.GREEN);
		_newGame.setIcon(new ImageIcon("./libs/1475912100_multimedia-19.png"));
		newGameAndReset.add(_newGame);
		_reset.setBackground(Color.RED);
		_reset.setIcon(new ImageIcon("./libs/1475912264_refresh.png"));
		newGameAndReset.add(_reset);

		scores.setLayout(scoreBoard);
		JLabel label1 = new JLabel("Naughts");
		label1.setFont(new Font("Yu Gothic Medium", Font.BOLD, 12));
		scores.add(label1 );
		JLabel label2 = new JLabel("Crosses");
		label2.setFont(new Font("Yu Gothic Medium", Font.BOLD, 12));
		scores.add(label2 );
		JLabel label3 = new JLabel("Draws");
		label3.setFont(new Font("Yu Gothic Medium", Font.BOLD, 12));
		scores.add( label3 );

		scores.add(_noOfNWins);
		scores.add(_noOfCWins);
		scores.add(_noOfDraws);

		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				inputPanel.add(_board[row][col]);
			}
		}

		layout.setLayout(boxLayout);
		layout.add(inputPanel);
		layout.add(newGameAndReset);
		layout.add(scores);

		add(layout);
	}

	// Helper method to determine the status of a Naughts and Crosses game. 
	// This method processes the 2 dimensional array of JButton objects, and 
	// uses the text value (NAUGHT, CROSS or BLANK) of each JButton to 
	// calculate the game's state of play.
	private GameStatus getGameStatus() {
		GameStatus status = GameStatus.DRAW;

		List<String> lines = new ArrayList<String>();

		// Top row
		lines.add(_board[0][0].getText() + _board[0][1].getText() + _board[0][2].getText());

		// Middle row
		lines.add(_board[1][0].getText() + _board[1][1].getText() + _board[1][2].getText());

		// Bottom row
		lines.add(_board[2][0].getText() + _board[2][1].getText() + _board[2][2].getText());

		// Left col
		lines.add(_board[0][0].getText() + _board[1][0].getText() + _board[2][0].getText());

		// Middle col
		lines.add(_board[0][1].getText() + _board[1][1].getText() + _board[2][1].getText());

		// Right col
		lines.add(_board[0][2].getText() + _board[1][2].getText() + _board[2][2].getText());

		// Diagonals
		lines.add(_board[0][0].getText() + _board[1][1].getText() + _board[2][2].getText());
		lines.add(_board[0][2].getText() + _board[1][1].getText() + _board[2][0].getText());

		// Check to see if there's any cell without a NAUGHT or a CROSS.
		for(String line : lines) {
			if(line.contains(BLANK)) {
				status = GameStatus.IN_PROGRESS;
				break;
			}
		}

		// Check to see if there's any line of NAUGHTs or CROSSes.
		if(lines.contains(CROSS + CROSS + CROSS)) {
			status = GameStatus.CROSS_WON; 

		} else if (lines.contains(NAUGHT + NAUGHT + NAUGHT)) {

			status = GameStatus.NAUGHT_WON;
		}

		return status;
	}

}
