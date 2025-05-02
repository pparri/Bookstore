package model;

// Static no porque sino se comparte
public class CartItem {
    public int bookId;
    public String title;
    public int quantity;
    public double price;

    public CartItem(int bookId, String title, int quantity, double price) {
        this.bookId = bookId;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
    }
}
