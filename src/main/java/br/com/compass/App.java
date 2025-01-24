package br.com.compass;

import br.com.compass.entities.Account;
import br.com.compass.entities.Transaction;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.repositories.TransactionRepository;
import br.com.compass.services.BankService;
import br.com.compass.services.exceptions.BankServiceException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EntityManager em = getEntityManager();

        AccountRepository accountRepository = new AccountRepository(em);
        TransactionRepository transactionRepository = new TransactionRepository(em);

        mainMenu(scanner);

        scanner.close();
        em.close();
        System.out.println("Application closed");
    }

    private static String loggedUser = "";

    public static void mainMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("========= Main Menu =========");
            System.out.println("|| 1. Login                ||");
            System.out.println("|| 2. Account Opening      ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            String option = scanner.next();

            switch (option) {
                case "1":
                    System.out.print("Email: ");
                    String emailLogin = scanner.next();
                    System.out.print("Password: ");
                    String passwordLogin = scanner.next();
                    if (AccountRepository.accountExists(emailLogin, passwordLogin)) {
                        loggedUser = emailLogin;
                        bankMenu(scanner);
                    } else {
                        System.out.println("Invalid email or password!");
                        break;
                    }
                    return;
                case "2":
                    System.out.print("Enter your name: ");
                    String name = scanner.next();

                    String email;
                    while (true) {
                        System.out.print("Enter your email: ");
                        email = scanner.next();

                        if (AccountRepository.emailExists(email)) {
                            System.out.println("Email already in use!");
                            continue;
                        }
                        break;
                    }

                    System.out.print("Enter your password: ");
                    String password = scanner.next();

                    String cpf;
                    while (true) {
                        System.out.print("Enter your CPF: ");
                        cpf = scanner.next();

                        if (AccountRepository.cpfExists(cpf)) {
                            System.out.println("Invalid CPF!");
                            continue;
                        }
                        break;
                    }

                    System.out.print("Enter your phone number: ");
                    String phone = scanner.next();

                    Pattern pattern = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([12][0-9]{3})$");
                    String birth;
                    while (true) {
                        System.out.print("Enter your date of birth (DD/MM/YYYY): ");
                        birth = scanner.next();
                        Matcher matcher = pattern.matcher(birth);

                        if (!matcher.matches()) {
                            System.out.println("Invalid format! Please enter the date in the format (DD/MM/YYYY).");
                            continue;
                        }
                        break;
                    }
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate birthDate = LocalDate.parse(birth, formatter);

                    Account account = new Account(name, phone, email, cpf, password, birthDate);

                    EntityManager em = getEntityManager();
                    em.getTransaction().begin();
                    em.persist(account);
                    em.getTransaction().commit();

                    System.out.print("Account created!");
                    System.out.println();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    public static void bankMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("========= Bank Menu =========");
            System.out.println("|| 1. Deposit              ||");
            System.out.println("|| 2. Withdraw             ||");
            System.out.println("|| 3. Check Balance        ||");
            System.out.println("|| 4. Transfer             ||");
            System.out.println("|| 5. Bank Statement       ||");
            System.out.println("|| 6. Logout               ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            String option = scanner.next();
            DecimalFormat df = new DecimalFormat("0.00");
            double value = 0;

            EntityManager em = getEntityManager();
            AccountRepository accountRepository = new AccountRepository(em);
            TransactionRepository transactionRepository = new TransactionRepository(em);
            BankService bankService = new BankService(accountRepository, transactionRepository);

            switch (option) {
                case "1":
                    while (true) {
                        System.out.print("Enter the  amount, or type (c) to cancel: R$");
                        String inp = scanner.next();
                        if (inp.equals("c")) {
                            break;
                        }
                        inp = inp.replace(",", ".");
                        try {
                            value = Double.parseDouble(inp);
                            if (value <= 0) {
                                System.out.println("Enter a value greater than zero.");
                                continue;
                            }
                            bankService.deposit(loggedUser, Double.parseDouble(inp));
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid entry! Please make sure to enter only numbers.");
                        }
                    }
                    break;
                case "2":
                    while (true) {
                        System.out.print("Enter the amount, or type (c) to cancel: R$");
                        String inp = scanner.next();
                        if (inp.equals("c")) {
                            break;
                        }
                        inp = inp.replace(",", ".");
                        try {
                            value = Double.parseDouble(inp);
                            if (value <= 0) {
                                System.out.println("Enter a value greater than zero.");
                                continue;
                            }
                            bankService.withdraw(loggedUser, Float.parseFloat(inp));
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input! Please make sure to enter only numbers.");
                        } catch (BankServiceException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case "3":
                    System.out.println("Balance: " + String.format("%.2f", accountRepository.findByEmail(loggedUser).getBalance()));
                    break;
                case "4":

                    System.out.print("Enter the amount, or type (c) to cancel: R$");
                    String inp = scanner.next();
                    if (inp.equals("c")) {
                        break;
                    }
                    inp = inp.replace(",", ".");
                    try {
                        value = Double.parseDouble(inp);
                        if (value <= 0) {
                            System.out.println("Enter a value greater than zero.");
                            continue;
                        }
                        System.out.print("Enter the receiver's email: ");
                        String receiver = scanner.next();
                        bankService.transfer(loggedUser, receiver, Float.parseFloat(inp));
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input! Please make sure to enter only numbers.");
                    } catch (BankServiceException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "5":
                    Account loggedAccount = accountRepository.findByEmail(loggedUser);
                    List<Transaction> transactions = bankService.getTransactionHistory(loggedAccount);
                    if (transactions.isEmpty()) {
                        System.out.println("No transactions found.");
                    } else {
                        System.out.println("Transaction History:");
                        DecimalFormat dfm = new DecimalFormat("#.00");
                        for (Transaction transaction : transactions) {
                            System.out.println("Transaction ID: " + transaction.getId() +
                                    " | Type: " + transaction.getTransactionType() +
                                    " | Value: R$" + String.format("%.2f", transaction.getValue()) +
                                    " | Date: " + transaction.getTransactionDate());
                        }
                    }
                    break;
                case "6":
                    loggedUser = "";
                    mainMenu(scanner);
                    return;
                case "0":
                    System.out.println("Exiting...");
                    running = false;
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static EntityManager getEntityManager() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BankPU");
        return emf.createEntityManager();
    }
}
