package br.com.compass.services;

import br.com.compass.entities.Account;
import br.com.compass.entities.enums.AccountType;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.repositories.TransactionRepository;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

public class MenuService {

    private final AccountRepository accountRepository;
    private final BankService bankService;
    private String loggedUser;

    public MenuService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.bankService = new BankService(accountRepository, transactionRepository);
        this.loggedUser = null;
    }

    public void mainMenu(Scanner scanner) {
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
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void login(Scanner scanner) {
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

    private void bankMenu(Scanner scanner) {
        boolean running = true;
        Account loggedAccount = AccountRepository.findByEmail(loggedUser);

        if (loggedAccount == null) {
            System.out.println("Error: Account not found.");
            loggedUser = null;
            return;
        }

        System.out.println("Welcome " + loggedAccount.getName() + "!");
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
                    handleDeposit(scanner);
                    break;
                case "2":
                    handleWithdraw(scanner);
                    break;
                case "3":
                    checkBalance();
                    break;
                case "4":
                    handleTransfer(scanner);
                    break;
                case "5":
                    viewTransactionHistory();
                    break;
                case "0":
                    loggedUser = null;
                    System.out.println("Logging out...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private void createAccount(Scanner scanner) {
        System.out.print("Enter your name: ");
        String name = scanner.next();

        String email = getEmail(scanner);
        String password = getPassword(scanner);
        String cpf = getCpf(scanner);
        System.out.print("Enter your phone number: ");
        String phone = scanner.next();
        LocalDate birthDate = getBirthDate(scanner);
        AccountType type = getAccountType(scanner, cpf);

        if (type == null) return;

        Account account = new Account(name, phone, email, cpf, password, birthDate, type);
        accountRepository.update(account);
        System.out.println("Account created successfully!");
    }

    private String getEmail(Scanner scanner) {
        while (true) {
            System.out.print("Enter your email: ");
            String email = scanner.next();

            if (AccountRepository.emailExists(email)) {
                System.out.println("Email already in use!");
            } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                System.out.println("Invalid email!");
            } else {
                return email;
            }
        }
    }

    private String getPassword(Scanner scanner) {
        System.out.print("Enter your password: ");
        return scanner.next();
    }

    private String getCpf(Scanner scanner) {
        while (true) {
            System.out.print("Enter your CPF (only numbers): ");
            String cpf = scanner.next();
            if (!cpf.matches("^\\d{11}$")) {
                System.out.println("Invalid CPF!");
            } else {
                return cpf;
            }
        }
    }

    private LocalDate getBirthDate(Scanner scanner) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            System.out.print("Enter your birth date (DD/MM/YYYY): ");
            String birthDate = scanner.next();
            try {
                return LocalDate.parse(birthDate, formatter);
            } catch (Exception e) {
                System.out.println("Invalid date format! Please try again.");
            }
        }
    }

    private AccountType getAccountType(Scanner scanner, String cpf) {
        while (true) {
            System.out.println("Choose your account type: ");
            System.out.println("1. Checking");
            System.out.println("2. Saving");
            System.out.println("3. Payroll");
            System.out.println("4. Student");
            System.out.println("5. Business");
            System.out.println("6. Cancel");

            String option = scanner.next();
            AccountType type;

            switch (option) {
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
                System.out.println("An account of this type already exists for this CPF!");
            } else {
                return type;
            }
        }
    }

    private void handleDeposit(Scanner scanner) {
        while (true) {
            System.out.print("Enter the  amount, or type (c) to cancel: R$");
            String inp = scanner.next();
            if (inp.equals("c")) {
                break;
            }
            DecimalFormat df = new DecimalFormat("0.00");
            double value = 0;
            inp = inp.replace(",", ".");
            try {
                value = Double.parseDouble(inp);
                if (value <= 0) {
                    System.out.println("Enter a value greater than zero.");
                    continue;
                }
                bankService.deposit(loggedUser, value);
                System.out.println("Deposit successful!");
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid entry! Please make sure to enter only numbers.");
            }
        }
    }

    private void handleWithdraw(Scanner scanner) {
        while (true) {
            System.out.print("Enter the  amount, or type (c) to cancel: R$");
            String inp = scanner.next();
            if (inp.equals("c")) {
                break;
            }
            DecimalFormat df = new DecimalFormat("0.00");
            double value = 0;
            inp = inp.replace(",", ".");
            try {
                value = Double.parseDouble(inp);
                if (value <= 0) {
                    System.out.println("Enter a value greater than zero.");
                    continue;
                }
                if (Objects.requireNonNull(AccountRepository.findByEmail(loggedUser)).getBalance() < value) {
                    System.out.println("Insufficient balance!");
                    continue;
                }
                bankService.withdraw(loggedUser, Double.parseDouble(inp));
                System.out.println("Withdraw successful!");
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid entry! Please make sure to enter only numbers.");
            }
        }
    }

    private void checkBalance() {
        double balance = Objects.requireNonNull(AccountRepository.findByEmail(loggedUser)).getBalance();
        System.out.printf("Your balance is: R$%.2f%n", balance);
    }

    private void handleTransfer(Scanner scanner) {
        while (true) {
            System.out.print("Enter the recipient's email, or type (c) to cancel: ");
            String recipientEmail = scanner.next();
            if (Objects.equals(loggedUser, recipientEmail)) {
                System.out.println("You can't transfer money to yourself!");
                continue;
            } else if (Objects.equals(recipientEmail, "c")) {
                return;
            }

            System.out.print("Enter the amount to transfer, or type (c) to cancel: R$");
            double amount = scanner.nextDouble();
            if (Objects.equals(recipientEmail, "c")) {
                return;
            } else if (amount <= 0) {
                System.out.println("Invalid amount!");
                continue;
            } else if (Objects.requireNonNull(AccountRepository.findByEmail(loggedUser)).getBalance() < amount) {
                System.out.println("Insufficient balance!");
                continue;
            }
            bankService.transfer(loggedUser, recipientEmail, amount);
            System.out.println("Transfer successful!");
            break;
        }
    }

    private void viewTransactionHistory() {
        Account account = AccountRepository.findByEmail(loggedUser);
        bankService.getTransactionHistory(account).forEach(transaction -> {
            System.out.printf("ID: %d | Type: %s | Amount: R$ %.2f | Date: %s%n",
                    transaction.getId(),
                    transaction.getTransactionType(),
                    transaction.getValue(),
                    transaction.getTransactionDate());
        });
    }
}
