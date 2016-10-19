package voxSpell.GUI;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import voxSpell.Quiz.Quiz;
import voxSpell.Quiz.QuizType;
import voxSpell.Quiz.WordList;
import voxSpell.Reward.NaughtsAndCrosses;
import voxSpell.Reward.RewardMediaPlayer;
import voxSpell.Sayable.FestivalSayable;
import voxSpell.Stats.Statistics;

/**
 * This class creates the GUI and required objects to run the program.
 * @author wayne
 * @editor Mike Lee
 */
public class SpellingAid implements ActionListener {

	private static File _videoFile = new File("./big_buck_bunny_1_minute.avi");;
	private static File _aechoVideoFile = new File("aecho_"+_videoFile.getName());;
	private int _level;
	private JProgressBar _progressBar;
	/**
	 * hides the progress bar.
	 */
	public void hideProgressBar() {
		_progressBar.setVisible(false);
		_progressBar.setValue(0);
	}
	/**
	 * sets the progress bar to the value specifics.
	 * the value must be between 0 and 100 inclusive.
	 * @param value
	 */
	public void updateProgressBar(int value) {
		if (value >= 0 && value <= 100) {
			_progressBar.setValue(value);
		}
	}
	private ArrayList<String> voiceOptions = new ArrayList<String>();
	private String _voice;
	private double _speed = 1.0;
	
	/**
	 * a getter method to return the double value of _speed.
	 * @return
	 */
	public double getSpeed() {
		return _speed;
	}

	// many many Swing components
	private JTextField inputText = new JTextField();
	/**
	 * disables the JTextFIeld.
	 */
	public void disableInputText() {
		inputText.setEnabled(false);
	}
	/**
	 * enables the JTextFIeld.
	 */
	public void enableInputText() {
		inputText.setEnabled(true);
	}

	private JButton newQuizBtn = new JButton("New Spelling Quiz");
	private JButton reviewMistakesBtn = new JButton("Review Mistakes");
	private JButton viewStatsBtn = new JButton("View Statistics");
	private JButton clearStatsBtn = new JButton("Clear Statistics");
	private JButton relistenToWord = new JButton("Listen to the word again.");
	/**
	 * disables the RelistenToWord button.
	 */
	public void disableRelistenToWord() {
		relistenToWord.setEnabled(false);
	}
	/**
	 * enables the RelistenToWord button.
	 */
	public void enableRelistenToWord() {
		relistenToWord.setEnabled(true);
	}

	private JPanel menuBtns = new JPanel(new GridLayout(0, 1));
	private JPanel inputArea = new JPanel(new GridLayout(0, 1));
	/**
	 * focuses on the JTextField.
	 */
	public void focusOnJPanel() {
		inputText.requestFocusInWindow();
	}
	private JPanel textAndButton = new JPanel();
	private JPanel mainScreen = new JPanel(new BorderLayout());
	private JPanel videoScreen = new JPanel(new BorderLayout());
	private JPanel overAllPanel = new JPanel(new CardLayout());
	private JPanel statsPanel = new JPanel(new BorderLayout());
	private JPanel previousInputPanel = new JPanel(new BorderLayout());
	private JPanel menuPanel = new JPanel(new BorderLayout());

	private JTextArea previousInput = new JTextArea("Please select one of the options to the left.");
	/**
	 * a getter method to get the JTextArea. 
	 * @return
	 */
	public JTextArea getPreviousInput() {
		return previousInput;
	}

	/**
	 * appendPreviousInput(String string) appends the specific string to the JTextArea.
	 * @param string
	 */
	public void appendPreviousInput(String string) {
		if (string.contains("ReviewMistakes")) {
			return;
		}
		this.previousInput.append(string);
	}

	private JLabel instructions = new JLabel();
	private JLabel statsTitle = new JLabel("Statistics (by Level)");
	private JLabel menuTitle = new JLabel("Menu");
	private JLabel previousInputTitle = new JLabel("Previous Input");

	private JTable _statsTable;
	private JScrollPane _scrollPane;

	//new stuff
	private JScrollPane previousInputScroll;

	private JFrame window;
	
	/**
	 * show the main GUI
	 */
	public void showWindow() {
		window.setVisible(true);
	}
	/**
	 * hide the main GUI
	 */
	public void hideWindow() {
		window.setVisible(false);
	}

	private JComboBox<String> voiceCBox;
	private JComboBox<Double> speedCBox;

	// Own Types
	private Statistics _stats;
	private Quiz _currentQuiz;
	private WordList _wordSource;
	private WordList _failedWords;

	/**
	 * Initializes swing components.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SpellingAid() {
		
		// set the default voice
		_voice = "kal_diphone";
		// say the welcome message
		ArrayList<String> temp = new ArrayList<String>();
		temp.add("Welcome to VoxSpell??");
		FestivalSayable.sayWord(temp, _speed, _voice);
		//Welcome Message
		JOptionPane.showMessageDialog(window, "Welcome to VoxSpell!\nPLease follow the instructions to set up the quiz.\nSelect the 'Cancel' options to use the default setting.");

		// Mostly configuring the Swing components here.
		// set up fonts for JLabels
		statsTitle.setFont(statsTitle.getFont().deriveFont(Font.BOLD));
		previousInputTitle.setFont(previousInputTitle.getFont().deriveFont(Font.BOLD));
		menuTitle.setFont(menuTitle.getFont().deriveFont(Font.BOLD));
		// configure the JFrame
		window = new JFrame("VoxSpell");
		window.setResizable(false);
		window.setSize(1200, 600);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// configure the relistenToWord button
		relistenToWord.setBackground(Color.CYAN);
		relistenToWord.setFont(relistenToWord.getFont().deriveFont(Font.BOLD));
		relistenToWord.setIcon(new ImageIcon(SpellingAid.class.getResource("/javax/swing/plaf/basic/icons/JavaCup16.png")));
		relistenToWord.addActionListener(this);
		// enable all the buttons
		enableAllButtons();

		newQuizBtn.setIcon(new ImageIcon(SpellingAid.class.getResource("/javax/swing/plaf/metal/icons/ocean/question.png")));
		newQuizBtn.setBackground(Color.GREEN);

		viewStatsBtn.setIcon(new ImageIcon(SpellingAid.class.getResource("/javax/swing/plaf/metal/icons/ocean/info.png")));

		reviewMistakesBtn.setIcon(new ImageIcon(SpellingAid.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));

		clearStatsBtn.setIcon(new ImageIcon(SpellingAid.class.getResource("/javax/swing/plaf/metal/icons/ocean/error.png")));
		clearStatsBtn.setBackground(Color.red);
		clearStatsBtn.setToolTipText("Are you sure?");
		
		inputText.addActionListener(this);
		inputText.setEnabled(false);
		inputText.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));

		menuTitle.setPreferredSize(new Dimension(70, 30));
		menuTitle.setHorizontalAlignment(SwingConstants.CENTER);
		menuTitle.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		menuBtns.add(newQuizBtn);
		menuBtns.add(reviewMistakesBtn);
		menuBtns.add(viewStatsBtn);
		menuBtns.add(clearStatsBtn);

		menuPanel.add(menuTitle, BorderLayout.NORTH);
		menuPanel.add(menuBtns, BorderLayout.CENTER);

		// configure the progress bar
		JPanel intermediatePanel = new JPanel(new BorderLayout() );
		_progressBar = new JProgressBar();
		_progressBar.setFont(_progressBar.getFont().deriveFont(Font.BOLD));
		_progressBar.setStringPainted(true);
		intermediatePanel.add(instructions, BorderLayout.WEST);
		_progressBar.setVisible(false);
		intermediatePanel.add(_progressBar, BorderLayout.EAST);
		inputArea.add(intermediatePanel);

		// new stuff
		inputText.setPreferredSize(new Dimension(450, 30));
		inputText.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));

		textAndButton.add(inputText);
		textAndButton.add(relistenToWord);
		
		//add speedCBox
		configureVoiceSpeed();
		textAndButton.add(speedCBox);
	
		inputArea.add(textAndButton);
		previousInputScroll = new JScrollPane(previousInput);

		previousInputTitle.setHorizontalAlignment(SwingConstants.CENTER);
		previousInputTitle.setPreferredSize(new Dimension(350, 30));
		previousInputTitle.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		instructions.setFont(new Font("Yu Gothic Medium", Font.BOLD, 16));

		previousInputPanel.add(previousInputTitle, BorderLayout.NORTH);
		previousInputPanel.add(previousInputScroll, BorderLayout.CENTER);

		mainScreen.add(inputArea, BorderLayout.SOUTH);
		mainScreen.add(menuPanel, BorderLayout.WEST);
		mainScreen.add(previousInputPanel, BorderLayout.CENTER);

		previousInput.setEditable(false);
		previousInput.setFont(new Font("Yu Gothic Medium", Font.PLAIN, 14));

		_failedWords = new WordList(new File(".failedWords"), QuizType.REVIEW);

		_stats = Statistics.getInstance();
		_statsTable = new JTable(_stats);
		_statsTable.setFont( new Font("Yu Gothic Medium", Font.PLAIN, 15)  );
		_statsTable.setEnabled(false);
		_statsTable.getColumnModel().getColumn(4).setPreferredWidth(100);

		_scrollPane = new JScrollPane(_statsTable);
		_scrollPane.setPreferredSize(new Dimension(400, 300));
		_statsTable.setFillsViewportHeight(true);
		_statsTable.setRowHeight(28);

		statsTitle.setHorizontalAlignment(SwingConstants.CENTER);
		statsTitle.setPreferredSize(new Dimension(350, 30));
		statsTitle.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		statsPanel.add(statsTitle, BorderLayout.NORTH);
		statsPanel.add(_scrollPane, BorderLayout.CENTER);

		mainScreen.add(statsPanel, BorderLayout.EAST);

		// configure fonts of JButtons
		newQuizBtn.setFont(newQuizBtn.getFont().deriveFont(Font.BOLD));
		reviewMistakesBtn.setFont(reviewMistakesBtn.getFont().deriveFont(Font.BOLD));
		viewStatsBtn.setFont(viewStatsBtn.getFont().deriveFont(Font.BOLD));
		clearStatsBtn.setFont(clearStatsBtn.getFont().deriveFont(Font.BOLD));

		overAllPanel.add(mainScreen, "MAIN");
		overAllPanel.add(videoScreen, "VIDEO");

		window.add(overAllPanel);
		// ask if the user wants to load own word list
		chooseOwnList();

		// add possible voice packages to check
		voiceOptions.add("kal_diphone");
		voiceOptions.add("akl_nz_jdt_diphone");
		voiceOptions.add("rab_diphone");
		voiceOptions.add("cmu_us_rms_arctic_clunits"); // Added _clunits so that it would work
		voiceOptions.add("cmu_us_slt_arctic_clunits");
		voiceOptions.add("cmu_us_bdl_arctic_clunits");
		voiceOptions.add("cmu_us_clb_arctic_clunits");
		voiceOptions.add("cmu_us_awb_cg");
		// ask if the user wants to load own video file
		chooseOwnVideo();
		// Process the echo video using FFMPEG at the start to ensure there is enough time
		if (checkVoice("ffmpeg")) {
			makeEchoVideo();
		}

		// Check if the voice packages exists.
		for (int i = 0; i < voiceOptions.size(); i++) {
			if (!checkVoice(voiceOptions.get(i) ) ) {
				voiceOptions.remove(i );
				i = -1;
			}
		}
		// configure the voice combo box
		voiceCBox = new JComboBox((String[]) voiceOptions.toArray(new String[0]) );
		voiceCBox.addActionListener(this);
		voiceCBox.setFont(voiceCBox.getFont().deriveFont(Font.BOLD));
		textAndButton.add(voiceCBox);
		voiceCBox.setToolTipText(voiceOptions.size()+" voices available");

		// Prompt the user to select a spelling level.
		selectLevel();

		// set up look and feel
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(window);
		// finally show the JFrame
		window.setVisible(true);
		// report level selected and prompt the user
		temp = new ArrayList<String>();
		temp.add("level "+_level+ "selected??");
		temp.add("Please select one of the options??");
		FestivalSayable.sayWord(temp, _speed, _voice);
		
	}

	/**
	 * Performs different actions, depending on what action was performed.
	 * @editor Mike Lee
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// get the source of action
		Object action = e.getSource();

		if (action.equals(newQuizBtn)) {
			// start a new quiz
			removeQuizListeners();
			_currentQuiz = new Quiz(_level, this, _wordSource); // change the input number here to change level for now
			inputText.addActionListener(_currentQuiz);

			_progressBar.setValue(0);
			_progressBar.setVisible(true);
			instructions.setText("Spell the word below and press Enter: ");
			inputText.setEnabled(true);
			relistenToWord.setEnabled(true);
			previousInput.setText("Level "+_level+"\n\n");
			previousInput.append("Please spell:   ");
			inputText.requestFocusInWindow();
			boolean status = _currentQuiz.sayNextWord(newQuizBtn.getText() );

			disableAllButtons();

			if (!status) { inputText.setEnabled(false); };
		} else if (action.equals(reviewMistakesBtn)) {
			// start a review quiz
			removeQuizListeners();
			_currentQuiz = new Quiz( _level, this, _failedWords);
			inputText.addActionListener(_currentQuiz);

			_progressBar.setValue(0);
			_progressBar.setVisible(true);
			instructions.setText("Spell the word below and press Enter: ");
			inputText.setEnabled(true);
			relistenToWord.setEnabled(true);
			previousInput.setText("Review Mistakes from Level "+_level+"\n\n");
			previousInput.append("Please spell:   ");
			inputText.requestFocusInWindow();
			boolean status = _currentQuiz.sayNextWord(reviewMistakesBtn.getText() );

			disableAllButtons();

			if (!status) {
				inputText.setEnabled(false);
				previousInput.setText(previousInput.getText().replace("Please spell:   ", "") );
			} 
		} else if (action.equals(viewStatsBtn)) {
			// prepare the view statistics screen
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(viewStatsBtn.getText()+"??");
			FestivalSayable.sayWord(temp, _speed, _voice);

			hideProgressBar();
			instructions.setText("");
			previousInput.setText("View Statistics for Level "+_level+"\n\n");
			disableInput();
			ArrayList<String> formattedStats = _stats.getStats();

			for (String line : formattedStats) {
				previousInput.append(line);
			}
		} else if (action.equals(clearStatsBtn)) {
			//say the confirm message
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(clearStatsBtn.getText()+"??");
			temp.add("are you sure??");
			FestivalSayable.sayWord(temp, _speed, _voice);
			// ask the user to confirm
			int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the statistics?");
			if (dialogResult == JOptionPane.YES_OPTION) {
				hideProgressBar();
				instructions.setText("");
				previousInput.setText("All Statistics Cleared!!");
				disableInput();
				_stats.clearStats();
				clearList(".failedlist");
				clearList(".faultedlist");
			} else {
				return;
			}		
			
		} else if (action.equals(relistenToWord)) {
			// repeat the word in festival
			if (_currentQuiz != null) {
				_currentQuiz.repeatWordWithNoPenalty();
			}
			inputText.requestFocusInWindow();
		} else if (action.equals(voiceCBox)) {
			// change the festival voice
			_voice = (String)voiceCBox.getSelectedItem();
			inputText.requestFocusInWindow();
		} else if (action.equals(speedCBox)) {
			// change the festival speed
			_speed = (double)speedCBox.getSelectedItem();
			inputText.requestFocusInWindow();
		} else {
			// clear the JTextField
			inputText.setText("");
		}
	}

	/**
	 * chooseOwnList() uses JFileChooser to allow the user to select a customised word list.
	 */
	private void chooseOwnList() {
		// make a JFileChooser that accepts text files only.
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt Files", "txt");
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);
		chooser.setFileFilter(filter);
		chooser.setDialogTitle("Choose Your Own Word List?");
		int returnVal = chooser.showOpenDialog((JFrame)window.getParent());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			_wordSource = new WordList(new File(chooser.getSelectedFile().getAbsolutePath()), QuizType.NEW);
			// show an example of the expected format
			JOptionPane.showMessageDialog(window, "Your word list must be in this format:\n%Level 1\na\nI\nit\nthe\nwas\nand\nin\nmy\nto\nwe\n......\nOtherwise errors may occur!!");
		}
		if (_wordSource == null) {
			// use the default word list if nothing chosen
			_wordSource = new WordList(new File("NZCER-spelling-lists.txt"), QuizType.NEW);
		}
	}

	/**
	 * chooseOwnMusic() uses JFileChooser to allow the user to select an mp3 file.
	 */
	private String chooseOwnMusic() {
		// make a JFileChooser that accepts mp3 files only.
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".mp3 Files", "mp3");
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);
		chooser.setFileFilter(filter);
		chooser.setDialogTitle("Choose Your Own Music File?");
		int returnVal = chooser.showOpenDialog((JFrame)window.getParent());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsoluteFile().getAbsolutePath() ;
		} else {
			// use the default music if nothing chosen
			return "./bensound-funnysong.mp3";
		}
	}

	/**
	 * chooseOwnVideo() uses JFileChooser to allow the user to select an avi file.
	 */
	private void chooseOwnVideo() {
		// make a JFileChooser that accepts avi files only.
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".avi Files", "avi");
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);
		chooser.setFileFilter(filter);
		chooser.setDialogTitle("Choose Your Own Video File?");
		int returnVal = chooser.showOpenDialog((JFrame)window.getParent());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			// use the custom video file
			_videoFile = new File(chooser.getSelectedFile().getAbsoluteFile().getAbsolutePath() );
			_aechoVideoFile = new File("aecho_"+_videoFile.getName());
		}
		if (_videoFile == null) {
			// use the default video if nothing chosen
			_videoFile = new File("./big_buck_bunny_1_minute.avi");
			_aechoVideoFile = new File("aecho_"+_videoFile.getName());
		}
	}

	/**
	 * getVoice() is a getter method to return the _voice field.
	 */
	public String getVoice() {
		return _voice;
	}

	/**
	 * checkVoice(String voice) checks if the voice specified exists using a ProcessBuilder.
	 */
	private boolean checkVoice(String voice) {
		try {
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", "locate " + voice);
			Process pro = pb.start();

			BufferedReader stdOut = new BufferedReader(new InputStreamReader(pro.getInputStream()));

			String line = stdOut.readLine();

			if (line != null) {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Clears the given list by deleting it and re-making it.
	 * @param list - the list the clear
	 */
	private void clearList(String list) {
		File listFile = new File(list);

		if (listFile.exists()) {
			listFile.delete();
			try {
				listFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Removes old Quiz ActionListeners from the input field.
	 */
	private void removeQuizListeners() {
		ActionListener[] listeners = inputText.getActionListeners();
		for (ActionListener listener : listeners) {
			if (listener != this) {
				inputText.removeActionListener(listener);
			}
		}
	}

	/**
	 * selectLevel() prompts the user to select a spelling level.
	 */
	private void selectLevel() {
		//Asking user for which spelling level they want to start with
		boolean isAnswer = false;
		while (!isAnswer) {
			Object[] levels = new String[_wordSource.numOfLevels()];
			for (int i = 0; i < _wordSource.numOfLevels(); i++) {
				levels[i] = "Level " + (i + 1);
			}

			String answer = (String)JOptionPane.showInputDialog(window, "Please pick a spelling level to start with: ", "Spelling Level", JOptionPane.QUESTION_MESSAGE, null, levels, levels[0]);
			if (answer != null) {
				int level = Integer.parseInt(answer.split(" ")[1]);
				if (level <= _wordSource.numOfLevels() ) {
					_level = level;

					break;
				}
			}
		}
	}

	/**
	 * levelCompleted() prompts the user to select one of the following: "Move up a Spelling level", "Stay at current Spelling level", "Play reward video", "Play reward video with Echo Effect"
	 * @editor Mike Lee
	 */
	public void levelCompleted() {
		
		//create pop up to ask user either move up, stay at level, or play video
		Object[] options = {"Move up a Spelling level", "Stay at current Spelling level", "Play reward video", "Play reward video with Echo Effect", "Play Tic-Tac-Toe", "Play music"};

		while (true) {

			int n = JOptionPane.showOptionDialog(window, "Please select an option:", "Congratulations!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

			if (n == 0) {
				if (_level < _wordSource.numOfLevels()) {
					_level++;
				}
				enableAllButtons();
				showWindow();
				
				ArrayList<String> temp = new ArrayList<String>();
				temp.add("PLease spell one of the options??");
				FestivalSayable.sayWord(temp, _speed, _voice);
				
				break;
			} else if (n == 1) {
				
				ArrayList<String> temp = new ArrayList<String>();
				temp.add("PLease spell one of the options??");
				FestivalSayable.sayWord(temp, _speed, _voice);
				
				enableAllButtons();
				showWindow();
				window.setResizable(false);
				break;
			} else if (n == 2) {
				if (_videoFile.exists()) {
					showWindow();
					playVideo(_videoFile.getName() );
					break;
				} else {
					JOptionPane.showMessageDialog(null, "Cannot find video file:  "+_videoFile.getName(), "Video File missing!", JOptionPane.ERROR_MESSAGE);
				}

			} else if (n == 3) {
				if (checkVoice("ffmpeg") && _aechoVideoFile.exists() ) {
					showWindow();
					playVideo(_aechoVideoFile.getName() );
					break;
				} else if (!checkVoice("ffmpeg")) {
					JOptionPane.showMessageDialog(null, "ffmpeg not installed.", "ffmpeg missing!", JOptionPane.ERROR_MESSAGE);
				} else if (!_aechoVideoFile.exists()) {
					JOptionPane.showMessageDialog(null, "Cannot find the video file.", "Video File missing!", JOptionPane.ERROR_MESSAGE);
				}
			} else if (n == 4) {
				new NaughtsAndCrosses(this);

				hideWindow();
				break;
			} else if (n == 5){
				showWindow();
				// Music from: http://www.bensound.com
				playVideo( chooseOwnMusic() );

				break;
			}
		}
	}


	/**
	 * playVideo(final String videoName) is a private helper method that that creates an instance of the RewardMediaPlayer and switches to the video JPanel.
	 */
	private void playVideo(final String videoName) {
		refreshVideoScreen();
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				NativeLibrary.addSearchPath(
						RuntimeUtil.getLibVlcLibraryName(), "/Applications/vlc-2.0.0/VLC.app/Contents/MacOS/lib"
						);
				Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

				new RewardMediaPlayer(videoScreen, SpellingAid.this, videoName);

				return null;
			}
		};	
		worker.execute();
		switchScreens("VIDEO");
	}

	/**
	 * refreshVideoScreen() is a private helper method that switches the current JPanel to the video JPanel.
	 */
	private void refreshVideoScreen() {
		overAllPanel.remove(videoScreen);
		videoScreen = new JPanel(new BorderLayout());
		overAllPanel.add(videoScreen, "VIDEO");
	}

	/**
	 * switchScreens(String screen) switches the JPanel for performing a quiz or playing a video. 
	 */
	public void switchScreens(String screen) {
		CardLayout cl = (CardLayout)(overAllPanel.getLayout());
		if (!screen.equals("") && !screen.equals("\n")) {
			cl.show(overAllPanel, screen);
		}
	}

	/**
	 * Private helper method to delete the echo video.
	 * @author Mike Lee
	 */
	private static void deleteEchoVideo() {	
		if (_aechoVideoFile.exists()) {
			_aechoVideoFile.delete();
		}
	}

	/**
	 * makeEchoVideo() is a private helper method to process the echo video in the background.
	 */
	private static void makeEchoVideo() {
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				if (!_aechoVideoFile.exists()) {
					ProcessBuilder pb = new ProcessBuilder("bash", "-c", "ffmpeg -i " +_videoFile.getName()+ " -af aecho -vf hue=s=0 "+_aechoVideoFile.getName());
					@SuppressWarnings("unused")
					Process pro = pb.start();
				}
				return null;	
			}
		};
		worker.execute();
	}

	/**
	 * enableInput() temporarily removes actionlistener to inputText and enables relistenToWord.
	 */
	public void disableInput() {
		this.removeQuizListeners();
		this.disableRelistenToWord();
	}

	/**
	 * enableInput() adds actionlistener to inputText and enables relistenToWord.
	 */
	public void enableInput() {
		inputText.addActionListener(_currentQuiz);
		relistenToWord.setEnabled(true);
	}

	/**
	 * disableOtherButtons(JButton exceptionButton) disables all other buttons apart from the given one.
	 * @param exceptionButton
	 */
	public void disableAllButtons() {

		newQuizBtn.removeActionListener(this);

		reviewMistakesBtn.removeActionListener(this);

		viewStatsBtn.removeActionListener(this);

		clearStatsBtn.removeActionListener(this);

		//exceptionButton.addActionListener(this);
	}

	/**
	 * enableAllButtons() enables the four main JButtons.
	 */
	public void enableAllButtons() {
		
		ActionListener[] temp1 = newQuizBtn.getActionListeners();	
		if (!checkThisAsListener(temp1) ) {
			newQuizBtn.addActionListener(this);
		}
		
		ActionListener[] temp2 = reviewMistakesBtn.getActionListeners();
		if (!checkThisAsListener(temp2) ) {
			reviewMistakesBtn.addActionListener(this);
		}
			
		ActionListener[] temp3 = viewStatsBtn.getActionListeners();
		if (!checkThisAsListener(temp3) ) {
			viewStatsBtn.addActionListener(this);
		}
			
		ActionListener[] temp4 = clearStatsBtn.getActionListeners();
		if (!checkThisAsListener(temp4) ) {
			clearStatsBtn.addActionListener(this);
		}

	}
	
	/**
	 * checkThisAsListener checks if this class is contained as an action listener in the array given.
	 * @param temp
	 * @return
	 */
	private boolean checkThisAsListener(ActionListener[] temp) {
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] == this) {
				return true;
			}
		}
		return false;
	}

	/**
	 * configureVoiceSpeed() is a private helper method that configures the voice speed ComboBox.
	 */
	private void configureVoiceSpeed() {
		
		Double[] tempArray = {0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0, 2.1, 2.2};	
		
		speedCBox = new JComboBox<Double>(tempArray);
		speedCBox.addActionListener(this);
		speedCBox.setFont(speedCBox.getFont().deriveFont(Font.BOLD));
		speedCBox.setToolTipText(tempArray.length+" speeds available");
		
	}
	
	/**
	 * Main static method to run the GUI.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@SuppressWarnings("unused")
			@Override
			public void run() {
				SpellingAid spell = new SpellingAid();
			}
		});

		// Delete the echo video when the user exits.
		// @author Mike Lee
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				deleteEchoVideo();
			}
		});
	} 
}

