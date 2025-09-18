/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package onlineshoopingmartsystem;

import java.util.List;

public class PaymentSystem {
    private DatabaseManager dbManager;

    public PaymentSystem(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public boolean processPayment(Order order, String paymentMethod) {
        System.out.println("Attempting to process payment for Order ID: " + order.getOrderId());
        System.out.println("Amount: $" + order.getTotal() + ", Method: " + paymentMethod);

        try {
            boolean paymentSuccess = simulatePaymentGateway(order.getTotal(), paymentMethod);

            if (paymentSuccess) {
                // Assuming recordPayment accepts (int orderId, double amount, String paymentMethod)
                dbManager.recordPayment(Integer.parseInt(order.getOrderId()), order.getTotal(), paymentMethod);
                System.out.println("Payment successful for Order ID: " + order.getOrderId());
                order.setStatus("Paid");
                return true;
            } else {
                System.out.println("Payment failed for Order ID: " + order.getOrderId());
                order.setStatus("Payment Failed");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error processing payment for Order ID: " + order.getOrderId() + ": " + e.getMessage());
            order.setStatus("Payment Error");
            return false;
        }
    }

    private boolean simulatePaymentGateway(double amount, String paymentMethod) {
        if (amount <= 0) {
            return false;
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            return false;
        }
        // Simulate success
        return true;
    }

    public void getAllPayments() {
        try {
            // Assuming dbManager.getAllPayments() returns List<Payment>
            List<Payment> payments = dbManager.getAllPayments();
            System.out.println("=== All Payments ===");
            for (Payment p : payments) {
                System.out.println("Payment ID: " + p.getPaymentId()
                        + ", Order ID: " + p.getOrderId()
                        + ", Username: " + p.getUsername()
                        + ", Amount: $" + p.getAmount()
                        + ", Date: " + p.getPaymentDate());
            }
        } catch (Exception e) {
            System.err.println("Error retrieving payments: " + e.getMessage());
        }
    }
}
