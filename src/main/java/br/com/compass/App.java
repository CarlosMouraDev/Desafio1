package br.com.compass;

import br.com.compass.entities.Account;
import br.com.compass.repositories.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    
    public static void main(String[] args) {
        EntityManager em = getEntityManager();

        AccountRepository accountRepository = new AccountRepository(em);

        Scanner scanner = new Scanner(System.in);

        mainMenu(scanner);
        
        scanner.close();
        em.close();
        System.out.println("Application closed");
    }

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
                            System.out.println("Formato inválido! Por favor, insira a data no formato DD/MM/YYYY.");
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

    private static EntityManager getEntityManager() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BankPU");
        return emf.createEntityManager();
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
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    // ToDo...
                    System.out.println("Deposit.");
                    break;
                case 2:
                    // ToDo...
                    System.out.println("Withdraw.");
                    break;
                case 3:
                    // ToDo...
                    System.out.println("Check Balance.");
                    break;
                case 4:
                    // ToDo...
                    System.out.println("Transfer.");
                    break;
                case 5:
                    // ToDo...
                    System.out.println("Bank Statement.");
                    break;
                case 0:
                    // ToDo...
                    System.out.println("Exiting...");
                    running = false;
                    return;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }
}
