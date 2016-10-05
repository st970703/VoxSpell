import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class test extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					test frame = new test();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public test() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setIcon(new ImageIcon(test.class.getResource("/javax/swing/plaf/basic/icons/JavaCup16.png")));
		btnNewButton.setBounds(5, 5, 440, 135);
		contentPane.add(btnNewButton);
		
		table = new JTable();
		table.setBounds(265, 191, 1, 1);
		contentPane.add(table);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("New radio button");
		rdbtnNewRadioButton.setBounds(44, 246, 149, 23);
		contentPane.add(rdbtnNewRadioButton);
		
		textField = new JTextField();
		textField.setBounds(65, 219, 114, 19);
		contentPane.add(textField);
		textField.setColumns(10);
	}
}
