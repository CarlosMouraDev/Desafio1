package br.com.compass.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String phone;
    private String email;
    private String cpf;
    private String password;
    private LocalDate birth;
    private double balance;

    public Account() {}
    public Account(String name, String phone, String email, String cpf, String password, LocalDate birth) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.cpf = cpf;
        this.birth = birth;
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

    public String getCpf() {
        return cpf;
    }
}
