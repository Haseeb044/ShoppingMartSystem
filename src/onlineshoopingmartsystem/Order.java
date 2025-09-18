/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package onlineshoopingmartsystem;

import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Order {
    private String orderId;
    private String username;
    private ShoppingCart cart;
    private String status;
    private double total;
    private Date orderDate;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Order(String orderId, String username, ShoppingCart cart, double total) {
        this.orderId = orderId;
        this.username = username;
        this.cart = cart;
        this.status = "Processing";
        this.total = total;
        this.orderDate = new Date(); // Set to current date/time when order is created
    }

    public String getOrderId() { return orderId; }
    public String getUsername() { return username; }
    public ShoppingCart getCart() { return cart; }
    public String getStatus() { return status; }
    public double getTotal() { return total; }
    public Date getOrderDate() { return orderDate; }
    public String getFormattedOrderDate() { return dateFormat.format(orderDate); }

    public String generateInvoiceString() {
        StringBuilder invoice = new StringBuilder();
        invoice.append("===== INVOICE ====\n");
        invoice.append("Order ID: ").append(orderId).append("\n");
        invoice.append("Customer: ").append(username).append("\n");
        invoice.append("Date: ").append(getFormattedOrderDate()).append("\n");
        invoice.append("Status: ").append(status).append("\n");
        invoice.append("-----------------------------\n");

        int itemNumber = 1;
        if (cart != null) {
            for (CartItem item : cart.getItems()) {
                invoice.append("Item ").append(itemNumber).append(": ")
                        .append(item.getProductName()).append(" (x").append(item.getQuantity()).append(") - $")
                        .append(String.format("%.2f", item.getPrice()))
                        .append(" Total: $").append(String.format("%.2f", (item.getPrice() * item.getQuantity())))
                        .append("\n");
                itemNumber++;
            }
        } else {
            invoice.append("No items in this order's cart.\n");
        }

        invoice.append("-----------------------------\n");
        invoice.append("TOTAL: $").append(String.format("%.2f", total)).append("\n");
        invoice.append("Thank you for your purchase!\n");
        return invoice.toString();
    }

    public void setStatus(String status) {
        this.status = status;
    }
}