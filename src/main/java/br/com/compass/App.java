package br.com.compass;

import br.com.compass.repositories.AccountRepository;
import br.com.compass.repositories.TransactionRepository;
import br.com.compass.services.MenuService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BankPU");
        EntityManager em = emf.createEntityManager();

        AccountRepository accountRepository = new AccountRepository(em);
        TransactionRepository transactionRepository = new TransactionRepository(em);

        MenuService menuService = new MenuService(accountRepository, transactionRepository);

        Scanner scanner = new Scanner(System.in);

        menuService.mainMenu(scanner);

        scanner.close();
        em.close();
        emf.close();
    }
}
