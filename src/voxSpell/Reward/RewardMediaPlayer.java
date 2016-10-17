package voxSpell.Reward;

/*
 * Example downloaded from the Nasser's ACP exercise.
 * Editor: En-Yu Mike Lee, 15/09/2016
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import voxSpell.GUI.SpellingAid;

/**
 * RewardMediaPlayer constructs a media player in the JPanel supplied.
 * @author mike
 */
public class RewardMediaPlayer{

	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private String _filename;

	/**
	 * The constructor of RewardMediaPlayer.
	 * @param panel - an external JPanel.
	 * @param parent - an object of SpellingAid Class.
	 * @param filename - a String.
	 */
	public RewardMediaPlayer(JPanel panel, final SpellingAid parent, String filename) {
		// Set the private fields
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		_filename = filename;

		final EmbeddedMediaPlayer video = mediaPlayerComponent.getMediaPlayer();

		// Add the media player to the JPanel
		panel.add(mediaPlayerComponent, BorderLayout.CENTER);

		// Add a listener to switch back to the main JPanel when the video finishes
		video.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				// Call the stop() method
				video.stop();
				// Switch to the main JPanel inside the main GUI
				parent.switchScreens("MAIN");
				// Show the option dialog again
				parent.levelCompleted();
			}
		});

		// A sub panel to contain all JButtons, GridLayout is used.
		JPanel subPanel = new JPanel(new GridLayout(6,1));       

		// Construct all JButtons and add Listeners
		// All icons from https://www.iconfinder.com/
		JButton btnMute = new JButton("Mute");
		btnMute.setFont(btnMute.getFont().deriveFont(Font.BOLD));
		btnMute.setIcon(new ImageIcon("./libs/1475851587_device-volume-loudspeaker-speaker-mute-glyph.png"));
		subPanel.add(btnMute);
		btnMute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				video.mute();
			}
		});

		JButton btnSkip = new JButton("Forward 5 seconds");
		btnSkip.setFont(btnSkip.getFont().deriveFont(Font.BOLD));
		btnSkip.setIcon(new ImageIcon("./libs/1475851869_skip-next.png"));
		subPanel.add(btnSkip);
		btnSkip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				video.skip(5000);
			}
		});

		JButton btnSkipBack = new JButton("Backward 5 seconds");
		btnSkipBack.setFont(btnSkipBack.getFont().deriveFont(Font.BOLD));
		btnSkipBack.setIcon(new ImageIcon("./libs/1475851745_skip-previous.png"));
		subPanel.add(btnSkipBack);
		btnSkipBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				video.skip(-5000);
			}
		});

		JButton btnPause = new JButton("Pause / Resume");
		btnPause.setFont(btnPause.getFont().deriveFont(Font.BOLD));
		btnPause.setIcon(new ImageIcon("./libs/1475851509_pause-circle-outline.png"));
		subPanel.add(btnPause);
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				video.pause();
			}
		});

		JButton btnStop = new JButton("Stop");
		btnStop.setFont(btnStop.getFont().deriveFont(Font.BOLD));
		btnStop.setIcon(new ImageIcon("./libs/1475851403_thefreeforty_hand.png"));
		subPanel.add(btnStop);
		btnStop.setBackground(Color.RED);
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				video.stop();
				parent.switchScreens("MAIN");
				parent.levelCompleted();
			}
		});

		JButton btnReplay = new JButton("Replay");
		btnReplay.setBackground(Color.GREEN);
		btnReplay.setFont(btnReplay.getFont().deriveFont(Font.BOLD));
		btnReplay.setIcon(new ImageIcon("./libs/1475850836_play-circle-outline.png"));

		subPanel.add(btnReplay);
		btnReplay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				video.playMedia(_filename);
				video.mute(false);
			}
		});

		// Add the JPanel containing all JButtons 
		panel.add(subPanel, BorderLayout.EAST);

		// Configure the display of video time
		final JLabel labelTime = new JLabel("0 seconds");
		panel.add(labelTime, BorderLayout.SOUTH);

		Timer timer = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				long time = (long)(video.getTime()/1000.0);
				long totalTime = (long)(video.getLength()/1000.0);
				labelTime.setText(String.valueOf(time)+ " / " + String.valueOf(totalTime)+" seconds                      "+_filename);
			}
		});
		timer.start();

		// Configure the JPanel
		panel.setLocation(100, 100);
		panel.setSize(1050, 600);
		panel.setVisible(true);

		// Play and un-mute the video
		video.playMedia(_filename);
		video.mute(false);
	}

}