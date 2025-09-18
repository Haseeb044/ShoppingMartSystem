/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package onlineshoopingmartsystem;

import java.util.LinkedList;
import java.util.Queue;

public class OrderQueue {
    Queue<Order> pendingOrders;

    public OrderQueue() {
        pendingOrders = new LinkedList<>();
    }

    public void addOrder(Order order) {
        pendingOrders.offer(order);
        System.out.println("Order " + order.getOrderId() + " added to queue.");
    }

    public Order processNextOrder() {
        if (!pendingOrders.isEmpty()) {
            Order order = pendingOrders.poll();
            System.out.println("Processing order " + order.getOrderId() + " from queue.");
            return order;
        } else {
            System.out.println("No pending orders to process.");
            return null;
        }
    }

    public boolean hasPendingOrders() {
        return !pendingOrders.isEmpty();
    }

    public int getQueueSize() {
        return pendingOrders.size();
    }
}