import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.table.TableModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.awt.Window;
import java.awt.event.*;

public class ExpenseCalculatorTest {

    private Connection connection;
    private ExpenseCalculator expenseCalculator = new ExpenseCalculator("Pranav");
    private LoginFrame loginFrame;
    private SignupFrame signupFrame;
    @Before
    public void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/expensecalculator", "root", "Pranav@2003");
        loginFrame = new LoginFrame();
        signupFrame = new SignupFrame();
    }

    @After
    public void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
    //Test if the username already exists
    @Test
    public void testUsernameExists() throws SQLException {
        String testUsername = "Pranav";
        boolean userExists = signupFrame.usernameExists(testUsername);
        assertTrue(userExists);
        testUsername = "Ragu";
        userExists = signupFrame.usernameExists(testUsername);
        assertFalse(userExists);
    }
    //Test for Successful Login
    @Test
    public void testLoginSuccess() {
        assertTrue(loginFrame.login("Pranav", "12345678"));
        assertFalse(loginFrame.login("Pikkaboo", "2349825"));
    }
    //Test for Failed Login
    @Test
    public void testLoginFailure() {
        assertFalse(loginFrame.login("invaliduser", "wrongpassword"));
    }
    //Test Inupt validity
    public void testValidateInputs() {
        assertTrue(signupFrame.validateInputs("Raj", "Raj@example.com", "25", "Male"));

        assertFalse(signupFrame.validateInputs("Pranav", "Pranav@example.com", "25", "Male"));

        assertFalse(signupFrame.validateInputs("Kumar", "mail", "25", "Male"));

        assertFalse(signupFrame.validateInputs("Ramkumar", "Ramkumar@example.com", "-25", "Male"));

        assertFalse(signupFrame.validateInputs("Gokul", "Gokul@example.com", "100", "Male"));

        assertFalse(signupFrame.validateInputs("Bala", "Bala@example.com", "25", "Trans"));
    }
    //Test for Successful Signup
    @Test
    public void testSignupSuccess() throws SQLException {
        String username = "newuser";
        String password = "newpassword";
        String email = "newuser@example.com";
        int age = 25;
        String gender = "Male";

        assertTrue(signupFrame.signup(username, password, email, age, gender));

        // Clean up
        String query = "DELETE FROM signup WHERE user_name=?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, username);
        stmt.executeUpdate();
        stmt.close();
    }
    // Denial of SignUp (Assuming the user already exists)
    @Test
    public void testSignupFailure() {

        assertFalse(signupFrame.signup("Pranav", "password", "email@example.com", 30, "Female"));
    }
    //Testing if Expenses are added properly
    @Test
    public void testAddExpense() {
        String username = "Pranav";
        expenseCalculator = new ExpenseCalculator(username);

        int initialRowCount = expenseCalculator.tableModel.getRowCount();

        expenseCalculator.addExpense("100", "Food", username);

        DefaultTableModel tableModel = (DefaultTableModel) expenseCalculator.tableModel;

        int finalRowCount = tableModel.getRowCount();

        assertEquals(initialRowCount + 1, finalRowCount);
        assertEquals(100.0, tableModel.getValueAt(finalRowCount - 1, 0));
        assertEquals("Food", tableModel.getValueAt(finalRowCount - 1, 2));
    }
    @Test
    public void testInvalidAmountFormat() {
        String username = "Pranav";
        expenseCalculator = new ExpenseCalculator(username);

        int initialRowCount = expenseCalculator.tableModel.getRowCount();
        JTextField amountField = new JTextField("Invalid amount format.");
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"Food"});

        expenseCalculator.addExpense(amountField.getText(), (String) categoryComboBox.getSelectedItem(), username);

        DefaultTableModel tableModel = (DefaultTableModel) expenseCalculator.tableModel;
        int finalRowCount = tableModel.getRowCount();
        assertEquals(initialRowCount, finalRowCount);
    }
    //Insight Page
    @Test
    public void testOpenInsightsPage() {
        String username = "Pranav";
        expenseCalculator = new ExpenseCalculator(username);

        // Register a window listener to intercept frame openings
        expenseCalculator.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                if (e.getWindow() instanceof JFrame) {
                    JFrame frame = (JFrame) e.getWindow();
                    if (frame.getTitle().equals("Expense Pie Chart")) {
                        assertTrue(true);
                    }
                }
            }
        });

        expenseCalculator.openInsightsPage(username);
    }
    //Test for Logout Butoon
    @Test
    public void testLogoutButton() {
        ExpenseCalculator expenseCalculator = new ExpenseCalculator("Pranav");

        JButton logoutButton = expenseCalculator.getLogoutButton();
        ActionListener[] actionListeners = logoutButton.getActionListeners();
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Logout"));
        }

        boolean loginFrameOpened = false;
        for (Window window : Window.getWindows()) {
            if (window instanceof LoginFrame) {
                loginFrameOpened = true;
                break;
            }
        }
        assertTrue(loginFrameOpened);
    }
    //Testcase for validationg total spending calculation
    @Test
    public void testCalculateTotalSpending() {
        String username = "Pranav";
        expenseCalculator = new ExpenseCalculator(username);

        double previousTotalSpending = 0.0;
        TableModel tableModel = expenseCalculator.getTableModel();
        int rowCount = tableModel.getRowCount();
        int amountColumnIndex = 0;

        for (int i = 0; i < rowCount; i++) {
            previousTotalSpending += Double.parseDouble(tableModel.getValueAt(i, amountColumnIndex).toString());
        }

        expenseCalculator.addExpense("100", "Food", username);
        expenseCalculator.addExpense("200", "Transport", username);

        assertEquals(previousTotalSpending + 100.0 + 200.0, expenseCalculator.calculateTotalSpending(), 0.01);
    }

    @Test
    public void testCloseButtonClosesAllFrames() {
        // Create some test frames
        JFrame frame1 = new JFrame("Frame 1");
        JFrame frame2 = new JFrame("Frame 2");
        JFrame frame3 = new JFrame("Frame 3");

        // Set the frames to visible
        frame1.setVisible(true);
        frame2.setVisible(true);
        frame3.setVisible(true);

        assertThat(frame1.isShowing()).isTrue();
        assertThat(frame2.isShowing()).isTrue();
        assertThat(frame3.isShowing()).isTrue();

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Window[] windows = Window.getWindows();
                for (Window window : windows) {
                    if (window instanceof JFrame) {
                        window.dispose();
                    }
                }
            }
        });

        closeButton.doClick();

        assertThat(frame1.isDisplayable()).isFalse();
        assertThat(frame2.isDisplayable()).isFalse();
        assertThat(frame3.isDisplayable()).isFalse();
    }
    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("ExpenseCalculatorTest");
    }
}
