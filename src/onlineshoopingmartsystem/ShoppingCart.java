/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package onlineshoopingmartsystem;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class ShoppingCart {
    private LinkedList<CartItem> items;
    private double total;

    public ShoppingCart() {
        items = new LinkedList<>();
        total = 0.0;
    }

    public void addItem(int id, String name, double price, int quantity) {
        for (CartItem item : items) {
            if (item.getProductId() == id) {
                item.setQuantity(item.getQuantity() + quantity);
                total += (price * quantity);
                System.out.println("Item quantity updated in cart.");
                return;
            }
        }
        CartItem newItem = new CartItem(id, name, price, quantity);
        items.add(newItem);
        total += (price * quantity);
        System.out.println("Item added to cart.");
    }

    public void removeItem(int id) {
        CartItem itemToRemove = null;
        for (CartItem item : items) {
            if (item.getProductId() == id) {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove != null) {
            total -= (itemToRemove.getPrice() * itemToRemove.getQuantity());
            items.remove(itemToRemove);
            System.out.println("Item removed from cart.");
        } else {
            System.out.println("Item not found in cart!");
        }
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public double getTotal() {
        return total;
    }

    public void clearCart() {
        items.clear();
        total = 0.0;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}