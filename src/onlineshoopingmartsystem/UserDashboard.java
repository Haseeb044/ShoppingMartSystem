/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package onlineshoopingmartsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserDashboard extends JPanel {
    private User currentUser;
    private DatabaseManager dbManager;
    private PaymentSystem paymentSystem;
    private OrderQueue orderQueue;
    private MainApp mainApp;

    private JTable productTable, cartTable, orderTable;
    private DefaultTableModel productTableModel, cartTableModel, orderTableModel;
    private JTextArea invoiceArea, paymentInvoiceArea;
    private JLabel cartTotalLabel;
    private JTextField productIdField, quantityField;

    private CardLayout cardLayout;
    private JPanel rightPanel;

    public UserDashboard(User user, DatabaseManager dbManager, PaymentSystem paymentSystem, OrderQueue orderQueue, MainApp mainApp) {
        this.currentUser = user;
        this.dbManager = dbManager;
        this.paymentSystem = paymentSystem;
        this.orderQueue = orderQueue;
        this.mainApp = mainApp;

        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setPreferredSize(new Dimension(200, getHeight()));
        leftPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setLayout(new GridLayout(0, 1, 0, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        String[] menuItems = {
            "Product Management", "Shopping Cart", "Order Management",
            "Payment System", "Order History", "Logout"
        };

        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setFocusPainted(false);
            button.setBackground(Color.DARK_GRAY);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 13));
            button.setPreferredSize(new Dimension(180, 40));
            button.addActionListener(e -> showPanel(item));
            buttonPanel.add(button);
        }

        leftPanel.add(buttonPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);
        rightPanel.setBackground(Color.WHITE);

        rightPanel.add(createProductPanel(), "Product Management");
        rightPanel.add(createCartPanel(), "Shopping Cart");
        rightPanel.add(createOrderPanel(), "Order Management");
        rightPanel.add(createPaymentPanel(), "Payment System");
        rightPanel.add(createOrderHistoryPanel(), "Order History");

        add(rightPanel, BorderLayout.CENTER);

        showPanel("Product Management");
        loadProducts();
        updateCartDisplay();
        loadUserOrders();
    }

    private void showPanel(String name) {
        if ("Logout".equals(name)) {
            mainApp.showUserLoginScreen();
        } else {
            cardLayout.show(rightPanel, name);
            if ("Order Management".equals(name)) {
                loadUserOrdersForManagement();
            } else if ("Payment System".equals(name)) {
                loadLatestInvoice();
            }
        }
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        productTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Category", "Price", "Stock"}, 0);
        productTable = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Color.WHITE);

        productIdField = new JTextField(5);
        quantityField = new JTextField(5);

        JButton addToCart = new JButton("Add to Cart");
        addToCart.setBackground(Color.BLACK);
        addToCart.setForeground(Color.WHITE);
        addToCart.addActionListener(e -> addToCart());

        inputPanel.add(new JLabel("Product ID:"));
        inputPanel.add(productIdField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(addToCart);

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        cartTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Price", "Qty", "Subtotal"}, 0);
        cartTable = new JTable(cartTableModel);
        panel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);

        cartTotalLabel = new JLabel("Total: $0.00");
        JButton remove = new JButton("Remove Selected");
        JButton checkout = new JButton("Checkout");

        remove.setBackground(Color.BLACK);
        remove.setForeground(Color.WHITE);
        remove.addActionListener(e -> removeFromCart());

        checkout.setBackground(Color.BLACK);
        checkout.setForeground(Color.WHITE);
        checkout.addActionListener(e -> checkout());

        bottom.add(cartTotalLabel);
        bottom.add(remove);
        bottom.add(checkout);

        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        orderTableModel = new DefaultTableModel(new Object[]{"Order ID", "Date", "Status", "Total"}, 0);
        orderTable = new JTable(orderTableModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton trackButton = new JButton("Track Selected Order");
        trackButton.setBackground(Color.BLACK);
        trackButton.setForeground(Color.WHITE);
        trackButton.addActionListener(e -> trackSelectedOrder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(trackButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        paymentInvoiceArea = new JTextArea();
        paymentInvoiceArea.setEditable(false);
        paymentInvoiceArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panel.add(new JScrollPane(paymentInvoiceArea), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Invoice");
        refreshButton.setBackground(Color.BLACK);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadLatestInvoice());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOrderHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        invoiceArea = new JTextArea();
        invoiceArea.setEditable(false);
        invoiceArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panel.add(new JScrollPane(invoiceArea), BorderLayout.CENTER);

        JButton refresh = new JButton("Refresh");
        refresh.setBackground(Color.BLACK);
        refresh.setForeground(Color.WHITE);
        refresh.addActionListener(e -> loadUserOrders());
        panel.add(refresh, BorderLayout.SOUTH);

        return panel;
    }

    private void loadProducts() {
        productTableModel.setRowCount(0);
        try {
            List<Product> products = dbManager.getAllProducts();
            for (Product p : products) {
                productTableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading products.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addToCart() {
        try {
            int id = Integer.parseInt(productIdField.getText().trim());
            int qty = Integer.parseInt(quantityField.getText().trim());

            Product p = dbManager.getProductById(id);
            if (p == null || qty <= 0 || p.getQuantity() < qty) {
                JOptionPane.showMessageDialog(this, "Invalid product or quantity.");
                return;
            }

            currentUser.getCart().addItem(id, p.getName(), p.getPrice(), qty);
            updateCartDisplay();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
        }
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) cartTableModel.getValueAt(row, 0);
            currentUser.getCart().removeItem(id);
            updateCartDisplay();
        }
    }

    private void updateCartDisplay() {
        cartTableModel.setRowCount(0);
        double total = 0;
        for (CartItem item : currentUser.getCart().getItems()) {
            double subtotal = item.getPrice() * item.getQuantity();
            total += subtotal;
            cartTableModel.addRow(new Object[]{
                item.getProductId(), item.getProductName(), item.getPrice(),
                item.getQuantity(), String.format("%.2f", subtotal)
            });
        }
        cartTotalLabel.setText("Total: $" + String.format("%.2f", total));
    }

    private void checkout() {
        if (currentUser.getCart().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty.");
            return;
        }

        double total = currentUser.getCart().getTotal();
        int confirm = JOptionPane.showConfirmDialog(this, "Total: $" + total + "\nConfirm checkout?", "Checkout", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            for (CartItem item : currentUser.getCart().getItems()) {
                Product p = dbManager.getProductById(item.getProductId());
                if (p == null || p.getQuantity() < item.getQuantity()) {
                    JOptionPane.showMessageDialog(this, "Stock issue with: " + item.getProductName());
                    return;
                }
                dbManager.updateProductStock(item.getProductId(), p.getQuantity() - item.getQuantity());
            }

            int orderId = dbManager.addOrder(currentUser.getUsername(), (int) total, currentUser.getCart().getItems());
            Order order = new Order(String.valueOf(orderId), currentUser.getUsername(), currentUser.getCart(), total);
            orderQueue.addOrder(order);

            boolean success = paymentSystem.processPayment(order, "Credit Card");
            if (success) {
                JOptionPane.showMessageDialog(this, "Order placed successfully!");
                currentUser.getCart().clearCart();
                updateCartDisplay();
                loadProducts();
                loadUserOrders();
                loadUserOrdersForManagement();
                loadLatestInvoice();
            } else {
                JOptionPane.showMessageDialog(this, "Payment failed.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Checkout failed.");
        }
    }

    private void loadUserOrders() {
        try {
            invoiceArea.setText("");
            List<Order> orders = dbManager.getOrdersByUsername(currentUser.getUsername());
            if (orders.isEmpty()) {
                invoiceArea.setText("No orders found.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Order o : orders) {
                    sb.append(o.generateInvoiceString()).append("\n\n");
                }
                invoiceArea.setText(sb.toString());
            }
        } catch (Exception e) {
            invoiceArea.setText("Error loading orders.");
        }
    }

    private void loadUserOrdersForManagement() {
        orderTableModel.setRowCount(0);
        try {
            List<Order> orders = dbManager.getOrdersByUsername(currentUser.getUsername());
            for (Order o : orders) {
                orderTableModel.addRow(new Object[]{
                    o.getOrderId(), o.getOrderDate(), o.getStatus(), "$" + String.format("%.2f", o.getTotal())
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading orders.");
        }
    }

    private void trackSelectedOrder() {
        int row = orderTable.getSelectedRow();
        if (row >= 0) {
            String orderId = (String) orderTableModel.getValueAt(row, 0);
            try {
                Order order = dbManager.getOrderById(orderId);
                if (order != null) {
                    JOptionPane.showMessageDialog(this, 
                        "Order ID: " + order.getOrderId() + "\n" +
                        "Status: " + order.getStatus() + "\n" +
                        "Date: " + order.getOrderDate() + "\n" +
                        "Total: $" + String.format("%.2f", order.getTotal()),
                        "Order Tracking", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error tracking order.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an order to track.");
        }
    }

    private void loadLatestInvoice() {
        try {
            List<Order> orders = dbManager.getOrdersByUsername(currentUser.getUsername());
            if (!orders.isEmpty()) {
                Order latestOrder = orders.get(orders.size() - 1);
                paymentInvoiceArea.setText(latestOrder.generateInvoiceString());
            } else {
                paymentInvoiceArea.setText("No invoices available.");
            }
        } catch (Exception e) {
            paymentInvoiceArea.setText("Error loading invoice.");
        }
    }
}

