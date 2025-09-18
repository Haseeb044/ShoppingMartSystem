/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package onlineshoopingmartsystem;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    private DatabaseManager dbManager;
    private OrderQueue orderQueue;
    private PaymentSystem paymentSystem;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainApp() {
        dbManager = new DatabaseManager();
        orderQueue = new OrderQueue();
        paymentSystem = new PaymentSystem(dbManager);

        setTitle("E-commerce System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        JPanel initialScreen = createInitialSelectionScreen();
        mainPanel.add(initialScreen, "Initial");

        showInitialScreen();
    }

    private JPanel createInitialSelectionScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Welcome to the E-commerce System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(60, 10, 20, 10));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 30, 15, 30);

        JButton userLoginButton = new JButton("User Login");
        userLoginButton.setPreferredSize(new Dimension(160, 45));
        userLoginButton.setBackground(Color.BLACK);
        userLoginButton.setForeground(Color.WHITE);
        userLoginButton.setFont(new Font("Arial", Font.BOLD, 16));
        userLoginButton.addActionListener(e -> showUserLoginScreen());

        JButton adminLoginButton = new JButton("Admin Login");
        adminLoginButton.setPreferredSize(new Dimension(160, 45));
        adminLoginButton.setBackground(Color.BLACK);
        adminLoginButton.setForeground(Color.WHITE);
        adminLoginButton.setFont(new Font("Arial", Font.BOLD, 16));
        adminLoginButton.addActionListener(e -> showAdminLoginScreen());

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(userLoginButton, gbc);
        gbc.gridx = 1;
        centerPanel.add(adminLoginButton, gbc);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    public void showInitialScreen() {
        cardLayout.show(mainPanel, "Initial");
        setVisible(true);
    }

    public void showUserLoginScreen() {
        UserLoginScreen userLoginScreen = new UserLoginScreen(dbManager, this);
        mainPanel.add(userLoginScreen, "UserLogin");
        cardLayout.show(mainPanel, "UserLogin");
    }

    public void showUserSignupScreen() {
        UserSignupScreen userSignupScreen = new UserSignupScreen(dbManager, this);
        mainPanel.add(userSignupScreen, "UserSignup");
        cardLayout.show(mainPanel, "UserSignup");
    }

    public void showAdminLoginScreen() {
        AdminLoginScreen adminLoginScreen = new AdminLoginScreen(dbManager, this);
        mainPanel.add(adminLoginScreen, "AdminLogin");
        cardLayout.show(mainPanel, "AdminLogin");
    }

    public void showUserDashboard(User user) {
        UserDashboard userDashboard = new UserDashboard(user, dbManager, paymentSystem, orderQueue, this);
        mainPanel.add(userDashboard, "UserDashboard");
        cardLayout.show(mainPanel, "UserDashboard");
    }

    public void showAdminDashboard(Admin admin) {
    AdminDashboard adminDashboard = new AdminDashboard(this);
    mainPanel.add(adminDashboard, "AdminDashboard");
    cardLayout.show(mainPanel, "AdminDashboard");
}

}



