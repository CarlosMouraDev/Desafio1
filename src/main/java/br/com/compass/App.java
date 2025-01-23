package br.com.compass;

import br.com.compass.entities.Account;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.services.BankService;
import br.com.compass.services.exceptions.BankServiceException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EntityManager em = getEntityManager();

        AccountRepository accountRepository = new AccountRepository(em);

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
                    System.out.print("Senha: ");
                    String passwordLogin = scanner.next();
                    if (AccountRepository.accountExists(emailLogin, passwordLogin)) {
                        loggedUser = emailLogin;
                        bankMenu(scanner);
                    } else {
                        System.out.println("Usuário inválido!");
                        break;
                    }
                    return;
                case "2":
                    System.out.print("Digite seu nome: ");
                    String name = scanner.next();

                    String email;
                    while (true) {
                        System.out.print("Digite seu email: ");
                        email = scanner.next();

                        if (AccountRepository.emailExists(email)) {
                            System.out.println("Email já registrado!");
                            continue;
                        }
                        break;
                    }

                    System.out.print("Digite sua senha: ");
                    String password = scanner.next();

                    String cpf;
                    while (true) {
                        System.out.print("Digite seu cpf: ");
                        cpf = scanner.next();

                        if (AccountRepository.cpfExists(cpf)) {
                            System.out.println("Cpf inválido!");
                            continue;
                        }
                        break;
                    }

                    System.out.print("Digite seu telefone: ");
                    String phone = scanner.next();

                    Pattern pattern = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([12][0-9]{3})$");
                    String birth;
                    while (true) {
                        System.out.print("Digite sua data de nascimento (DD/MM/YYYY): ");
                        birth = scanner.next();
                        Matcher matcher = pattern.matcher(birth);

                        if (!matcher.matches()) {
                            System.out.println("Formato inválido! Por favor, insira a data no formato (DD/MM/YYYY).");
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

                    System.out.print("Conta criada com sucesso!");
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
            BankService bankService = new BankService(accountRepository);

            switch (option) {
                case "1":
                    while (true) {
                        System.out.print("Digite o valor do depósito, ou digite (c) para cancelar: R$");
                        String inp = scanner.next();
                        if (inp.equals("c")) {
                            break;
                        }
                        inp = inp.replace(",", ".");
                        try {
                            value = Double.parseDouble(inp);
                            if (value <= 0) {
                                System.out.println("Digite um valor maior que zero.");
                                continue;
                            }
                            bankService.deposit(loggedUser, Float.parseFloat(inp));
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Entrada inválida! Certifique-se de digitar apenas números.");
                        }
                    }
                    break;
                case "2":
                    while (true) {
                        System.out.print("Digite o valor de saque, ou digite (c) para cancelar: R$");
                        String inp = scanner.next();
                        if (inp.equals("c")) {
                            break;
                        }
                        inp = inp.replace(",", ".");
                        try {
                            value = Double.parseDouble(inp);
                            if (value <= 0) {
                                System.out.println("Digite um valor maior que zero.");
                                continue;
                            }
                            bankService.withdraw(loggedUser, Float.parseFloat(inp));
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Entrada inválida! Certifique-se de digitar apenas números.");
                        } catch (BankServiceException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case "3":
                    System.out.println("Seu saldo: " + accountRepository.findByEmail(loggedUser).getBalance());
                    break;
                case "4":
                    // ToDo...
                    System.out.println("Transfer.");
                    break;
                case "5":
                    // ToDo...
                    System.out.println("Bank Statement.");
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
