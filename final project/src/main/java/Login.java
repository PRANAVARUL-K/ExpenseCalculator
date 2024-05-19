import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField username_stok;
	private JPasswordField pwdPassword;
	private JTextField user;
	private JPasswordField passwordField;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Login() {
		// Window setup
		setTitle("Login");
		setIconImage(new ImageIcon(getClass().getResource("/img_login/icons_password-64.png")).getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 640);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		Dimension dim = getToolkit().getScreenSize();
		setLocation(dim.width / 2 - getWidth() / 2, dim.height / 2 - getHeight() / 2);

		// Login panel components
		JButton signinbtn = new JButton("Login");
		signinbtn.setBounds(451, 371, 261, 50);
		signinbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signin();
			}
		});

		signinbtn.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					signinbtn.doClick();
				}
			}
		});

		signinbtn.setBackground(Color.YELLOW);
		signinbtn.setForeground(Color.RED);
		signinbtn.setFont(new Font("Tahoma", Font.BOLD, 21));
		contentPane.add(signinbtn);

		user = new JTextField("Enter The Username");
		user.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				user.setForeground(Color.BLACK);
				user.setText(null);
			}
		});

		user.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					passwordField.requestFocusInWindow();
				}
			}
		});
		user.setFont(new Font("Times New Roman", Font.ITALIC, 29));
		user.setForeground(Color.LIGHT_GRAY);
		user.setBounds(451, 183, 261, 62);
		contentPane.add(user);
		user.setColumns(20);

		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Times New Roman", Font.PLAIN, 29));
		passwordField.setBounds(451, 277, 261, 62);
		contentPane.add(passwordField);
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					signinbtn.doClick();
				}
			}
		});

		JLabel label = new JLabel("");
		label.setBounds(352, 267, 64, 71);
		label.setIcon(new ImageIcon(getClass().getResource("/img_login/icons_password-64.png")));
		contentPane.add(label);

		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBounds(352, 183, 64, 71);
		lblNewLabel_1.setIcon(new ImageIcon(getClass().getResource("/img_login/icons_user2-64.png")));
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_3 = new JLabel("");
		lblNewLabel_3.setIcon(new ImageIcon(getClass().getResource("/img_login/LOGO240_240PX.png")));
		lblNewLabel_3.setBounds(98, 183, 227, 241);
		contentPane.add(lblNewLabel_3);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon(getClass().getResource("/img_login/images.jpg")));
		lblNewLabel.setBounds(-6, -16, 900, 679);
		contentPane.add(lblNewLabel);
	}

	public void signin() {
		String UserName = new String("A");
		String Password = new String("A");
		String enteredUserName = user.getText();
		String enteredPassword = new String(passwordField.getPassword());

		if (UserName.matches(enteredUserName) && Password.matches(enteredPassword)) {
			dispose();
			JOptionPane.showMessageDialog(null, "Login Successful!");
		} else {
			JOptionPane.showMessageDialog(null, "Username/Password Error", "ERROR", JOptionPane.ERROR_MESSAGE);
			user.setText(null);
			passwordField.setText(null);
			user.requestFocusInWindow();
		}
	}
}
