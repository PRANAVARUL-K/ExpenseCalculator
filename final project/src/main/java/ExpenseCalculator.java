import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import java.util.regex.Pattern;

public class ExpenseCalculator extends JFrame {
    protected static final String JDBC_URL = "jdbc:mysql://localhost:3306/expensecalculator";
    protected static final String USERNAME = "root";
    protected static final String PASSWORD = "Pranav@2003";

    public DefaultTableModel tableModel;
    private JLabel welcomeLabel;
    public JLabel totalSpendingLabel;

    private JButton logoutButton;

    public ExpenseCalculator(String username) {
        setTitle("Expense Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create table model with columns: Amount, Date, Category
        tableModel = new DefaultTableModel(new Object[]{"Amount", "Date", "Category"}, 0);
        JTable expenseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        double a = 0;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/expensecalculator", "root", "Pranav@2003");
            String res = "SELECT * from expenses where user_name='"+username+"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(res);
            while(rs.next())
            {
                tableModel.addRow(new Object[]{rs.getDouble("amount"),rs.getTimestamp("money_date"),rs.getString("category")});
                a = a+rs.getDouble("amount");
            }

        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        // Create buttons
        JButton addButton = new JButton("Add New Expense");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAddExpenseDialog(username);
            }
        });

        JButton insightsButton = new JButton("Insights");
        insightsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openInsightsPage(username);
            }
        });

        logoutButton = new JButton("Logout");

        logoutButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // Close all open frames
                Window[] windows = getWindows();
                for (Window window : windows) {
                    if (window instanceof JFrame) {
                        window.dispose();
                    }
                }


                // Open the login page
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }

        });


        // Create welcome panel
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomePanel.add(welcomeLabel);

        // Create total spending panel
        JPanel totalSpendingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalSpendingLabel = new JLabel("Total Spending: ₹"+a);
        totalSpendingPanel.add(totalSpendingLabel);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(insightsButton);
        buttonPanel.add(logoutButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        add(totalSpendingPanel, BorderLayout.SOUTH);
    }
    public JButton getLogoutButton() {
        return logoutButton;
    }
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    void openAddExpenseDialog(String username) {
        // Create add expense dialog
        JDialog addExpenseDialog = new JDialog(this, "Add New Expense", true);
        addExpenseDialog.setSize(400, 200);
        addExpenseDialog.setLocationRelativeTo(this);

        // Create labels and text fields
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField(10);

        JLabel categoryLabel = new JLabel("Category:");
        String[] categories = {"Food", "Transport", "Grocery","Medical" ,"Education","Entertainment","Bills","Rent","Sports","Bank","Other"};
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);

        // Create add button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    if (amount <= 0) {
                        throw new NumberFormatException("Amount must be positive.");
                    }
                    addExpense(amountField.getText(), (String) categoryComboBox.getSelectedItem(), username);
                    addExpenseDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addExpenseDialog, "Please enter a valid positive amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create dialog layout
        JPanel dialogPanel = new JPanel(new GridLayout(3, 2));
        dialogPanel.add(amountLabel);
        dialogPanel.add(amountField);
        dialogPanel.add(categoryLabel);
        dialogPanel.add(categoryComboBox);
        dialogPanel.add(new JLabel());
        dialogPanel.add(addButton);

        addExpenseDialog.add(dialogPanel);
        addExpenseDialog.setVisible(true);
    }

    void addExpense(String amountText, String category,String username) {
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String query = "INSERT INTO expenses (user_name,amount, category) VALUES (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setDouble(2, amount);
            statement.setString(3, category);
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Add the expense to the table
        tableModel.addRow(new Object[]{amount, new java.util.Date(), category});

        // Update total spending label
        double totalSpending = calculateTotalSpending();
        totalSpendingLabel.setText("Total Spending: ₹" + String.format("%.2f", totalSpending));
    }

    double calculateTotalSpending() {
        double totalSpending = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            double amount = (double) tableModel.getValueAt(i, 0);
            totalSpending += amount;
        }
        return totalSpending;
    }

    void openInsightsPage(String u_name) {
        String url = "jdbc:mysql://localhost:3306/expensecalculator"; // Replace with your Oracle database URL
        String username = "root"; // Replace with your Oracle database username
        String password = "Pranav@2003"; // Replace with your Oracle database password

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // 1. Load the Oracle JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Create a connection to the Oracle database
            connection = DriverManager.getConnection(url, username, password);

            // 3. Create a statement object to execute SQL queries
            statement = connection.createStatement();

            // 4. Execute the SQL query to fetch data from the expenses table
            String sqlQuery = "SELECT category, SUM(amount) AS total_amount FROM expenses where user_name='"+u_name+"'GROUP BY category";
            resultSet = statement.executeQuery(sqlQuery);

            // 5. Create a dataset to hold the category and total amount data
            DefaultPieDataset dataset = new DefaultPieDataset();
            while (resultSet.next()) {
                String category = resultSet.getString("category");
                double totalAmount = resultSet.getDouble("total_amount");
                dataset.setValue(category, totalAmount);
            }

            // 6. Create the pie chart using the dataset
            JFreeChart chart = ChartFactory.createPieChart("Expense Breakdown", dataset, true, true, false);

            // 7. Customize the appearance of the chart
            chart.getTitle().setPaint(Color.BLACK);
            chart.getTitle().setFont(new Font("Arial", Font.BOLD, 18));
            //chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 12));

            // 8. Create a chart frame and display the chart
            ChartFrame frame = new ChartFrame("Expense Pie Chart", chart);
            frame.pack();
            frame.setVisible(true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 9. Close the resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}

class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Expense Calculator - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel signUpLabel = new JLabel("Don't have an account?");

        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);
        inputPanel.add(new JLabel());
        inputPanel.add(signUpLabel);

        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Signup");
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Close all open frames
                Window[] windows = getWindows();
                for (Window window : windows) {
                    if (window instanceof JFrame) {
                        window.dispose();
                    }
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (login(username, password)) {
                    openExpenseCalculator(username);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openSignupPage();
                dispose();
            }
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);
        buttonPanel.add(closeButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    boolean login(String username, String password) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/expensecalculator", "root", "Pranav@2003");
            String res = "SELECT * from signup where user_name='"+username+"' and password='"+password+"'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(res);
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void openExpenseCalculator(String username) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ExpenseCalculator expenseCalculator = new ExpenseCalculator(username);
                expenseCalculator.setVisible(true);
            }
        });
    }

    private void openSignupPage() {
        SignupFrame signupFrame = new SignupFrame();
        signupFrame.setVisible(true);
    }
}

class SignupFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField ageField;
    private JComboBox<String> genderComboBox;

    public SignupFrame() {
        setTitle("Expense Calculator - Signup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        JLabel ageLabel = new JLabel("Age:");
        ageField = new JTextField();
        JLabel genderLabel = new JLabel("Gender:");
        genderComboBox = new JComboBox<>(new String[]{"Select", "Male", "Female", "Other"});

        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);
        inputPanel.add(emailLabel);
        inputPanel.add(emailField);
        inputPanel.add(ageLabel);
        inputPanel.add(ageField);
        inputPanel.add(genderLabel);
        inputPanel.add(genderComboBox);
        inputPanel.add(new JLabel());
        inputPanel.add(new JLabel());

        JPanel buttonPanel = new JPanel();
        JButton signupButton = new JButton("Signup");
        JButton backButton = new JButton("Back");

        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText();
                String ageStr = ageField.getText();
                String gender = (String) genderComboBox.getSelectedItem();

                if (validateInputs(username, email, ageStr, gender)) {
                    if (signup(username, password, email, Integer.parseInt(ageStr), gender)) {
                        openExpenseCalculator(username);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(SignupFrame.this, "Failed to signup.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openLoginPage();
                dispose();
            }
        });

        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    boolean validateInputs(String username, String email, String ageStr, String gender) {
        if (username.isEmpty() || email.isEmpty() || ageStr.isEmpty() || gender.equals("Select")) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age >= 90) {
                JOptionPane.showMessageDialog(this, "Age must be between 1 and 89.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    boolean signup(String username, String password, String email, int age, String gender) {
        if(usernameExists(username))
            return false;
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/expensecalculator", "root", "Pranav@2003");

             PreparedStatement ps = con.prepareStatement("INSERT INTO signup (user_name, password, mail, age, gender) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setInt(4, age);
            ps.setString(5, gender);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    boolean usernameExists(String username) {
        boolean exists = false;
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/expensecalculator", "root", "Pranav@2003");
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM signup WHERE user_name = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                exists = rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return exists;
    }

    private void openExpenseCalculator(String username) {
        SwingUtilities.invokeLater(() -> {
            ExpenseCalculator expenseCalculator = new ExpenseCalculator(username);
            expenseCalculator.setVisible(true);
        });
    }

    private void openLoginPage() {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}