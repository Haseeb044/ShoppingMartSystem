/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package onlineshoopingmartsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminDashboard extends JPanel {
    private JPanel leftPanel, rightPanel;
    private JButton userBtn, productBtn, orderBtn, paymentBtn, stockBtn, logoutBtn;
    private JTable table;
    private DefaultTableModel tableModel;
    private DatabaseManager db;
    private MainApp mainApp;

    // Product form fields
    private JTextField idField, nameField, categoryField, priceField, quantityField,descriptionField;
    private JButton addBtn, updateBtn, deleteBtn;

    public AdminDashboard(MainApp mainApp) {
        this.mainApp = mainApp;
        db = new DatabaseManager();
        setLayout(new BorderLayout());

        leftPanel = new JPanel();
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setPreferredSize(new Dimension(200, 0));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        leftPanel.add(Box.createVerticalGlue());

        userBtn = createButton("User Management");
        productBtn = createButton("Product Management");
        orderBtn = createButton("Order Management");
        paymentBtn = createButton("Payment System");
        stockBtn = createButton("Stock Management");
        logoutBtn = createButton("Logout");

        leftPanel.add(userBtn);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(productBtn);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(orderBtn);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(paymentBtn);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(stockBtn);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(logoutBtn);
        leftPanel.add(Box.createVerticalGlue());

        add(leftPanel, BorderLayout.WEST);

        rightPanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);

        userBtn.addActionListener(e -> showUsers());
        productBtn.addActionListener(e -> showProducts());
        orderBtn.addActionListener(e -> showOrders());
        paymentBtn.addActionListener(e -> showPayments());
        stockBtn.addActionListener(e -> showStock());
        logoutBtn.addActionListener(e -> logout());
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        Dimension size = new Dimension(200, 70);
        btn.setMinimumSize(size);
        btn.setMaximumSize(size);
        btn.setPreferredSize(size);
        btn.setFocusPainted(false);
        btn.setBackground(Color.BLACK);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        return btn;
    }

    private void showUsers() {
        try {
            List<User> users = db.getAllUsers();
            tableModel.setRowCount(0);
            tableModel.setColumnIdentifiers(new Object[]{"Username", "Password"});
            for (User user : users) {
                tableModel.addRow(new Object[]{user.getUsername(), user.getPassword()});
            }
        } catch (SQLException ex) {
            showError(ex.getMessage());
        }
    }

private void showProducts() {
    rightPanel.removeAll();
    rightPanel.setLayout(new BorderLayout());

    JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
    JTextField idField = new JTextField(); // Hidden field to store ID
    idField.setVisible(false);

    nameField = new JTextField();
    categoryField = new JTextField();
    priceField = new JTextField();
    quantityField = new JTextField();

    formPanel.add(idField);
    formPanel.add(new JLabel()); // Empty label for alignment
    formPanel.add(new JLabel("Name:"));
    formPanel.add(nameField);
    formPanel.add(new JLabel("Category:"));
    formPanel.add(categoryField);
    formPanel.add(new JLabel("Price:"));
    formPanel.add(priceField);
    formPanel.add(new JLabel("Quantity:"));
    formPanel.add(quantityField);

    addBtn = new JButton("Add");
    updateBtn = new JButton("Update");
    deleteBtn = new JButton("Delete");

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addBtn);
    buttonPanel.add(updateBtn);
    buttonPanel.add(deleteBtn);

    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(formPanel, BorderLayout.CENTER);
    topPanel.add(buttonPanel, BorderLayout.SOUTH);

    rightPanel.add(topPanel, BorderLayout.NORTH);

    tableModel.setRowCount(0);
    tableModel.setColumnIdentifiers(new Object[]{"ID", "Name", "Category", "Price", "Quantity"});
    table = new JTable(tableModel);
    rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);

    loadProductTable();

    addBtn.addActionListener(e -> {
    try {
        String name = nameField.getText();
        String category = categoryField.getText();
        double price = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());

        if (name.isEmpty() || category.isEmpty()) {
            showError("Please fill all fields.");
            return;
        }

        db.addProduct(name, category, (int) price, quantity); 
        loadProductTable();
        nameField.setText("");
        categoryField.setText("");
        priceField.setText("");
        quantityField.setText("");
        idField.setText("");
    } catch (Exception ex) {
        showError("Invalid input.");
    }
});


    updateBtn.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        try {
            if (idField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Select a product to update.");
                return;
            }

            String name = nameField.getText();
            String category = categoryField.getText();
            String priceText = priceField.getText();
            String quantityText = quantityField.getText();

            if (name.isEmpty() || category.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled out.");
                return;
            }

            int id = Integer.parseInt(idField.getText());
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            db.updateProduct(id, name, category, (int) price, quantity); 

            loadProductTable();
            JOptionPane.showMessageDialog(null, "Product updated successfully!");

            idField.setText("");
            nameField.setText("");
            categoryField.setText("");
            priceField.setText("");
            quantityField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid price or quantity format.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error updating product: " + ex.getMessage());
        }
    }
});


    deleteBtn.addActionListener(e -> {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this product?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    db.deleteProduct(id);
                    loadProductTable();
                    idField.setText("");
                    nameField.setText("");
                    categoryField.setText("");
                    priceField.setText("");
                    quantityField.setText("");
                } catch (SQLException ex) {
                    Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            showError("Select a product to delete.");
        }
    });

    table.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent e) {
            int row = table.getSelectedRow();
            idField.setText(tableModel.getValueAt(row, 0).toString());
            nameField.setText(tableModel.getValueAt(row, 1).toString());
            categoryField.setText(tableModel.getValueAt(row, 2).toString());
            priceField.setText(tableModel.getValueAt(row, 3).toString());
            quantityField.setText(tableModel.getValueAt(row, 4).toString());
        }
    });

    revalidate();
    repaint();
}

    private void loadProductTable() {
        try {
            List<Product> products = db.getAllProducts();
            tableModel.setRowCount(0);
            for (Product p : products) {
                tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity()});
            }
        } catch (SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void showOrders() {
    rightPanel.removeAll();
    rightPanel.setLayout(new BorderLayout());

    tableModel.setRowCount(0);
    tableModel.setColumnIdentifiers(new Object[]{"Order ID", "Username", "Order Status", "Total"});
    
    try {
        List<Order> orders = db.getAllOrders();
        for (Order o : orders) {
            tableModel.addRow(new Object[]{o.getOrderId(), o.getUsername(), o.getStatus(), o.getTotal()});
        }
    } catch (SQLException ex) {
        showError(ex.getMessage());
    }

    table = new JTable(tableModel);
    rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);

    revalidate();
    repaint();
}

private void showPayments() {
    rightPanel.removeAll();
    rightPanel.setLayout(new BorderLayout());

    tableModel.setRowCount(0);
    tableModel.setColumnIdentifiers(new Object[]{"Payment ID", "Order ID", "Username", "Amount", "Date"});

    try {
        List<Payment> payments = db.getAllPayments();
        for (Payment p : payments) {
            tableModel.addRow(new Object[]{p.getPaymentId(), p.getOrderId(), p.getUsername(), p.getAmount(), p.getPaymentDate()});
        }
    } catch (SQLException ex) {
        showError(ex.getMessage());
    }

    table = new JTable(tableModel);
    rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);

    revalidate();
    repaint();
}

private void showStock() {
    rightPanel.removeAll();
    rightPanel.setLayout(new BorderLayout());

    tableModel.setRowCount(0);
    tableModel.setColumnIdentifiers(new Object[]{"ID", "Name", "Category", "Price", "Quantity"});

    try {
        List<Product> products = db.getAllProducts();
        for (Product p : products) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity()});
        }
    } catch (SQLException ex) {
        showError(ex.getMessage());
    }

    table = new JTable(tableModel);
    rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);

    revalidate();
    repaint();
}


    private void logout() {
        mainApp.showAdminLoginScreen();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, "Error: " + message);
    }
}



