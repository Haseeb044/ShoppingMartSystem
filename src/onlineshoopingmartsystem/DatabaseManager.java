/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package onlineshoopingmartsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Connection connection;
    private String dbUrl = "jdbc:mysql://localhost:3306/onlineshoppingmartsystem?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private String dbUser = "root";
    private String dbPassword = "Haseeb044@";

    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            System.out.println("Database Connection Successful");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database!");
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database Connection Closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing the database connection!");
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public void addProduct(String name, String category, int price, int stock) throws SQLException {
    String sql = "INSERT INTO Products (ProductName, Category, Price, Stock) VALUES (?, ?, ?, ?)";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, name);
        pstmt.setString(2, category);
        pstmt.setInt(3, price);
        pstmt.setInt(4, stock);
        pstmt.executeUpdate();
    }
}

public void updateProduct(int productId, String newName, String newCategory, int newPrice, int newStock) throws SQLException {
    String sql = "UPDATE Products SET ProductName = ?, Category = ?, Price = ?, Stock = ? WHERE ProductID = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, newName);
        pstmt.setString(2, newCategory);
        pstmt.setInt(3, newPrice);
        pstmt.setInt(4, newStock);
        pstmt.setInt(5, productId);
        pstmt.executeUpdate();
    }
}

public void deleteProduct(int productId) throws SQLException {
    String sql = "DELETE FROM Products WHERE ProductID = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, productId);
        pstmt.executeUpdate();
    }
}


    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT ProductID, ProductName, Category, Price, Stock FROM Products";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("ProductID");
                String name = rs.getString("ProductName");
                String category = rs.getString("Category");
                double price = rs.getInt("Price");
                int quantity = rs.getInt("Stock");
                products.add(new Product(id, name, category, price, quantity));
            }
        }
        return products;
    }

    public Product getProductById(int productId) throws SQLException {
        String sql = "SELECT ProductID, ProductName, Category, Price, Stock FROM Products WHERE ProductID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("ProductName");
                    String category = rs.getString("Category");
                    double price = rs.getInt("Price");
                    int quantity = rs.getInt("Stock");
                    return new Product(productId, name, category, price, quantity);
                }
            }
        }
        return null;
    }

    public void updateProductStock(int productId, int newStock) throws SQLException {
        String sql = "UPDATE Products SET Stock = ? WHERE ProductID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newStock);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        }
    }

    public void addUser(String userName, String firstName, String lastName, String password) throws SQLException {
        String sql = "INSERT INTO Users (UserName, FirstName, LastName, Password) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, password);
            pstmt.executeUpdate();
            System.out.println("User added successfully to DB!");
        }
    }

    public boolean checkUserCredentials(String userName, String password) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE UserName = ? AND Password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean userExists(String userName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE UserName = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<User> getAllUsers() throws SQLException {
    List<User> users = new ArrayList<>();
    String sql = "SELECT UserName, Password FROM Users";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
            users.add(new User(rs.getString("UserName"), rs.getString("Password")));
        }
    }
    return users;
}


    public int addOrder(String userName, double totalAmount, List<CartItem> cartItems) throws SQLException {
        String orderSql = "INSERT INTO Orders (UserName, OrderDate, TotalAmount) VALUES (?, ?, ?)";
        String orderDetailSql = "INSERT INTO OrderDetails (OrderID, ProductID, Quantity, PriceAtPurchase) VALUES (?, ?, ?, ?)";
        int orderId = -1;

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, userName);
                pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pstmt.setDouble(3, totalAmount);
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
                System.out.println("Order added to DB with ID: " + orderId + "!");
            }

            try (PreparedStatement pstmtDetails = conn.prepareStatement(orderDetailSql)) {
                for (CartItem item : cartItems) {
                    pstmtDetails.setInt(1, orderId);
                    pstmtDetails.setInt(2, item.getProductId());
                    pstmtDetails.setInt(3, item.getQuantity());
                    pstmtDetails.setDouble(4, item.getPrice());
                    pstmtDetails.addBatch();
                }
                pstmtDetails.executeBatch();
                System.out.println("Order details added to DB!");
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return orderId;
    }

    public List<Order> getOrdersByUsername(String userName) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String orderSql = "SELECT OrderID, UserName, OrderDate, TotalAmount FROM Orders WHERE UserName = ?";
        String orderDetailSql = "SELECT ProductID, Quantity, PriceAtPurchase FROM OrderDetails WHERE OrderID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmtOrders = conn.prepareStatement(orderSql)) {
            pstmtOrders.setString(1, userName);
            try (ResultSet rsOrders = pstmtOrders.executeQuery()) {
                while (rsOrders.next()) {
                    int orderId = rsOrders.getInt("OrderID");
                    Timestamp orderDate = rsOrders.getTimestamp("OrderDate");
                    double totalAmount = rsOrders.getDouble("TotalAmount");

                    ShoppingCart cart = new ShoppingCart();

                    try (PreparedStatement pstmtDetails = conn.prepareStatement(orderDetailSql)) {
                        pstmtDetails.setInt(1, orderId);
                        try (ResultSet rsDetails = pstmtDetails.executeQuery()) {
                            while (rsDetails.next()) {
                                int productId = rsDetails.getInt("ProductID");
                                int quantity = rsDetails.getInt("Quantity");
                                double priceAtPurchase = rsDetails.getDouble("PriceAtPurchase");

                                String productName = "Unknown Product";
                                Product p = getProductById(productId);
                                if (p != null) {
                                    productName = p.getName();
                                }
                                cart.addItem(productId, productName, priceAtPurchase, quantity);
                            }
                        }
                    }
                    Order order = new Order(String.valueOf(orderId), userName, cart, totalAmount);
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    public void recordPayment(int orderId, double amountPaid, String paymentMethod) throws SQLException {
        String sql = "INSERT INTO Payments (OrderID, PaymentDate, AmountPaid, PaymentMethod) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmt.setDouble(3, amountPaid);
            pstmt.setString(4, paymentMethod);
            pstmt.executeUpdate();
            System.out.println("Payment recorded successfully for order " + orderId + "!");
        }
    }
    // NEW: Fetch all orders with user and total info (for Admin Order Management Panel)
public List<Order> getAllOrders() throws SQLException {
    List<Order> orders = new ArrayList<>();
    String sql = "SELECT OrderID, UserName, OrderDate, TotalAmount FROM Orders";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
            int orderId = rs.getInt("OrderID");
            String userName = rs.getString("UserName");
            Timestamp orderDate = rs.getTimestamp("OrderDate");
            double totalAmount = rs.getDouble("TotalAmount");

            ShoppingCart cart = new ShoppingCart(); // Optional placeholder if details needed
            Order order = new Order(String.valueOf(orderId), userName, cart, totalAmount);
            orders.add(order);
        }
    }
    return orders;
}

public List<Payment> getAllPayments() throws SQLException {
    List<Payment> payments = new ArrayList<>();
    String sql = "SELECT PaymentID, OrderID, UserName, AmountPaid, PaymentDate FROM Payments";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
            int paymentId = rs.getInt("PaymentID");
            int orderId = rs.getInt("OrderID");
            String username = rs.getString("UserName");
            double amount = rs.getDouble("AmountPaid");
            Timestamp paymentTimestamp = rs.getTimestamp("PaymentDate");
            java.util.Date paymentDate = null;
            if (paymentTimestamp != null) {
                paymentDate = new java.util.Date(paymentTimestamp.getTime());
            }
            payments.add(new Payment(paymentId, orderId, username, amount, paymentDate));
        }
    }
    return payments;
}

public Order getOrderById(String orderId) throws SQLException {
    String orderSql = "SELECT OrderID, UserName, OrderDate, TotalAmount FROM Orders WHERE OrderID = ?";
    String orderDetailSql = "SELECT ProductID, Quantity, PriceAtPurchase FROM OrderDetails WHERE OrderID = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmtOrders = conn.prepareStatement(orderSql)) {
        
        pstmtOrders.setInt(1, Integer.parseInt(orderId));
        
        try (ResultSet rsOrders = pstmtOrders.executeQuery()) {
            if (rsOrders.next()) {
                String userName = rsOrders.getString("UserName");
                Timestamp orderDate = rsOrders.getTimestamp("OrderDate");
                double totalAmount = rsOrders.getDouble("TotalAmount");

                ShoppingCart cart = new ShoppingCart();

                try (PreparedStatement pstmtDetails = conn.prepareStatement(orderDetailSql)) {
                    pstmtDetails.setInt(1, Integer.parseInt(orderId));
                    try (ResultSet rsDetails = pstmtDetails.executeQuery()) {
                        while (rsDetails.next()) {
                            int productId = rsDetails.getInt("ProductID");
                            int quantity = rsDetails.getInt("Quantity");
                            double priceAtPurchase = rsDetails.getDouble("PriceAtPurchase");

                            String productName = "Unknown Product";
                            Product p = getProductById(productId);
                            if (p != null) {
                                productName = p.getName();
                            }
                            cart.addItem(productId, productName, priceAtPurchase, quantity);
                        }
                    }
                }

                return new Order(orderId, userName, cart, totalAmount);
            }
        }
    }
    return null;
}

}
