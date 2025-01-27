package br.com.compass;

import br.com.compass.entities.Account;
import br.com.compass.entities.Transaction;
import br.com.compass.entities.enums.AccountType;
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
                    login(scanner);
                    break;
                case "2":
                    createAccount(scanner);
                    break;
                case "0":
                    running = false;
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static void login(Scanner scanner) {
        System.out.print("Email: ");
        String emailLogin = scanner.next();
        System.out.print("Password: ");
        String passwordLogin = scanner.next();
        if (AccountRepository.accountExists(emailLogin, passwordLogin)) {
            loggedUser = emailLogin;
            bankMenu(scanner);
        } else {
            System.out.println("Invalid email or password!");
        }
    }

    private static void createAccount(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.next();

        String email = getEmail(scanner);
        String password = getPassword(scanner);
        String cpf = getCpf(scanner);
        String phone = getPhone(scanner);
        LocalDate birthDate = getBirthDate(scanner);

        AccountType type = getAccountType(scanner, cpf);

        if (type == null) return;

        Account account = new Account(name, phone, email, cpf, password, birthDate, type);

        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.persist(account);
        em.getTransaction().commit();

        System.out.println("Account created!");
    }

    private static String getEmail(Scanner scanner) {
        String email;
        while (true) {
            System.out.print("Enter your email: ");
            email = scanner.next();

            if (AccountRepository.emailExists(email)) {
                System.out.println("Email already in use!");
                continue;
            } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                System.out.println("Invalid email!");
                continue;
            }
            break;
        }
        return email;
    }

    private static String getPassword(Scanner scanner) {
        System.out.print("Enter your password: ");
        return scanner.next();
    }

    private static String getCpf(Scanner scanner) {
        String cpf;
        while (true) {
            System.out.print("Enter your CPF (only numbers): ");
            cpf = scanner.next();

            if (!cpf.matches("^\\d{11}$")) {
                System.out.println("Invalid CPF!");
                continue;
            }
            break;
        }
        return cpf;
    }

    private static String getPhone(Scanner scanner) {
        System.out.print("Enter your phone number: ");
        return scanner.next();
    }

    private static LocalDate getBirthDate(Scanner scanner) {
        String birth;
        Pattern pattern = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([12][0-9]{3})$");
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
        return LocalDate.parse(birth, formatter);
    }

    private static AccountType getAccountType(Scanner scanner, String cpf) {
        AccountType type = null;
        while (true) {
            System.out.println("Choose your account type: ");
            System.out.println("========== Account type =========");
            System.out.println("|| 1. Checking                 ||");
            System.out.println("|| 2. Saving                   ||");
            System.out.println("|| 3. Payroll                  ||");
            System.out.println("|| 4. Student                  ||");
            System.out.println("|| 5. Business                 ||");
            System.out.println("|| 6. Cancel Account Creation  ||");
            System.out.println("=================================");
            System.out.print("Choose an option: ");
            String opt = scanner.next();

            switch (opt) {
                case "1":
                    type = AccountType.CHECKING;
                    break;
                case "2":
                    type = AccountType.SAVING;
                    break;
                case "3":
                    type = AccountType.PAYROLL;
                    break;
                case "4":
                    type = AccountType.STUDENT;
                    break;
                case "5":
                    type = AccountType.BUSINESS;
                    break;
                case "6":
                    return null;
                default:
                    System.out.println("Invalid option!");
                    continue;
            }

            if (AccountRepository.accountCpfTypeExists(cpf, type)) {
                System.out.println("There's already an account with this type associated with that CPF number!");
                continue;
            }
            break;
        }
        return type;
    }


    public static void bankMenu(Scanner scanner) {
        boolean running = true;
        EntityManager em = getEntityManager();
        AccountRepository accountRepository = new AccountRepository(em);
        TransactionRepository transactionRepository = new TransactionRepository(em);
        BankService bankService = new BankService(accountRepository, transactionRepository);

        System.out.println("Welcome " + AccountRepository.findByEmail(loggedUser).getName() + "!");
        while (running) {
            System.out.println("========= Bank Menu =========");
            System.out.println("|| 1. Deposit              ||");
            System.out.println("|| 2. Withdraw             ||");
            System.out.println("|| 3. Check Balance        ||");
            System.out.println("|| 4. Transfer             ||");
            System.out.println("|| 5. Bank Statement       ||");
            System.out.println("|| 0. Logout               ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");
            String option = scanner.next();
            switch (option) {
                case "1":
                    handleDeposit(scanner, bankService);
                    break;
                case "2":
                    handleWithdraw(scanner, bankService);
                    break;
                case "3":
                    checkBalance(accountRepository);
                    break;
                case "4":
                    handleTransfer(scanner, bankService);
                    break;
                case "5":
                    viewTransactionHistory(accountRepository, bankService);
                    break;
                case "0":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static void handleDeposit(Scanner scanner, BankService bankService) {
        while (true) {
            System.out.print("Enter the amount, or type (c) to cancel: R$");
            String inp = scanner.next();
            if (inp.equals("c")) {
                break;
            }
            inp = inp.replace(",", ".");
            try {
                double value = Double.parseDouble(inp);
                if (value <= 0) {
                    System.out.println("Enter a value greater than zero.");
                    continue;
                }
                bankService.deposit(loggedUser, value);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid entry! Please make sure to enter only numbers.");
            }
        }
    }

    private static void handleWithdraw(Scanner scanner, BankService bankService) {
        while (true) {
            System.out.print("Enter the amount, or type (c) to cancel: R$");
            String inp = scanner.next();
            if (inp.equals("c")) {
                break;
            }
            inp = inp.replace(",", ".");
            try {
                double value = Double.parseDouble(inp);
                if (value <= 0) {
                    System.out.println("Enter a value greater than zero.");
                    continue;
                }
                bankService.withdraw(loggedUser, value);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please make sure to enter only numbers.");
            } catch (BankServiceException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void checkBalance(AccountRepository accountRepository) {
        System.out.println("Balance: " + String.format("%.2f", accountRepository.findByEmail(loggedUser).getBalance()));
    }

    private static void handleTransfer(Scanner scanner, BankService bankService) {
        System.out.print("Enter the amount, or type (c) to cancel: R$");
        String inp = scanner.next();
        if (inp.equals("c")) {
            return;
        }
        inp = inp.replace(",", ".");
        try {
            double value = Double.parseDouble(inp);
            if (value <= 0) {
                System.out.println("Enter a value greater than zero.");
                return;
            }
            System.out.print("Enter the receiver's email: ");
            String receiver = scanner.next();
            bankService.transfer(loggedUser, receiver, value);
            System.out.println("Successfully transferred!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please make sure to enter only numbers.");
        } catch (BankServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewTransactionHistory(AccountRepository accountRepository, BankService bankService) {
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
    }

    private static void logout(Scanner scanner) {
        loggedUser = "";
        mainMenu(scanner);
    }


    private static EntityManager getEntityManager() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BankPU");
        return emf.createEntityManager();
    }
}
