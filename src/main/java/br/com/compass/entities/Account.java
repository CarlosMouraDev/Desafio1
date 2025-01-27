package br.com.compass.entities;

import br.com.compass.entities.enums.AccountType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phone;

    private String email;

    private String cpf;

    private String password;

    private LocalDate birth;

    private double balance;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    public Account() {}

    public Account(String name, String phone, String email, String cpf, String password, LocalDate birth, AccountType accountType) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.cpf = cpf;
        this.birth = birth;
        this.password = password;
        this.balance = 0;
        this.accountType = accountType;
    }

    public void increaseBalance(double quantity) {
        if (quantity > 0) {
            this.balance += quantity;
        }
    }

    public double getBalance() {
        return balance;
    }

    public void decreaseBalance(double quantity) {
        this.balance -= quantity;
    }

    public Long getId() {
        return id;
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

    public AccountType getAccountType() {
        return accountType;
    }

    public LocalDate getBirth() {
        return birth;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}