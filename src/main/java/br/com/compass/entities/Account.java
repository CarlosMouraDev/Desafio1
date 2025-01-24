package br.com.compass.entities;

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

    // Getters para os atributos
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

    public LocalDate getBirth() {
        return birth;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Verifica se é o mesmo objeto
        if (o == null || getClass() != o.getClass()) return false; // Verifica se não é nulo e se são da mesma classe
        Account account = (Account) o;
        return Objects.equals(id, account.id); // Compara os IDs das contas
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Gera um hash baseado no ID
    }
}