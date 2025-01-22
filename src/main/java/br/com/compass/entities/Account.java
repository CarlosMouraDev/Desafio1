package br.com.compass.entities;

public class Account {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String password;
    private double balance;

    public Account() {}
    public Account(String name, String phone, String email, String password) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.balance = 0;
    }

    public double getBalance() {
        return balance;
    }

    public void increaseBalance(double quantity) {
        if (quantity > 0) {
            this.balance += quantity;
        }
    }

    public void decreaseBalance(double quantity) {
        if (quantity > this.balance) {
            this.balance -= quantity;
        }
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
