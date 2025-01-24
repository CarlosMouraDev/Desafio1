package br.com.compass.services;

import br.com.compass.entities.Account;
import br.com.compass.entities.Transaction;
import br.com.compass.entities.enums.TransactionType;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.repositories.TransactionRepository;
import br.com.compass.services.exceptions.BankServiceException;

import java.util.List;
import java.util.stream.Collectors;

public class BankService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public BankService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;

    }

    public void deposit(String email, double amount)  {
        Account account = accountRepository.findByEmail(email);
        account.increaseBalance(amount);
        Transaction transaction = new Transaction();
        transaction.setOriginAccount(account);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setValue(amount);
        transaction.setTransactionDate(java.time.LocalDate.now());
        accountRepository.update(account);
        transactionRepository.save(transaction);
    }

    public void withdraw(String email, double amount)  throws BankServiceException {
        Account account = accountRepository.findByEmail(email);
        if (account.getBalance() < amount) {
            throw new BankServiceException("Insufficient funds.");
        }

        account.decreaseBalance(amount);
        accountRepository.update(account);

        Transaction transaction = new Transaction();
        transaction.setOriginAccount(account);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setValue(amount);
        transaction.setTransactionDate(java.time.LocalDate.now());
        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionHistory(Account loggedUser) {
        List<Transaction> allTransactions = transactionRepository.findTransactionsByAccount(loggedUser);
        return allTransactions.stream()
                .filter(transaction ->
                        (transaction.getOriginAccount().equals(loggedUser) &&
                                (transaction.getTransactionType() == TransactionType.DEPOSIT ||
                                        transaction.getTransactionType() == TransactionType.WITHDRAW ||
                                        transaction.getTransactionType() == TransactionType.SENT_TRANSFER)) ||
                                (transaction.getDestinationAccount().equals(loggedUser) &&
                                        transaction.getTransactionType() == TransactionType.RECEIVED_TRANSFER)
                )
                .collect(Collectors.toList());
    }

    public void transfer(String recipient, String receiver, double amount) throws BankServiceException {
        Account account1 = accountRepository.findByEmail(recipient);
        Account account2 = accountRepository.findByEmail(receiver);

        if (account2 == null) {
            throw new BankServiceException("Account not found.");
        }

        if (account1.getBalance() < amount) {
            throw new BankServiceException("Insufficient funds.");
        }
        
        account1.decreaseBalance(amount);
        account2.increaseBalance(amount);

        Transaction transactionSent = new Transaction();
        transactionSent.setOriginAccount(account1);
        transactionSent.setDestinationAccount(account2);
        transactionSent.setTransactionType(TransactionType.SENT_TRANSFER);
        transactionSent.setValue(amount);
        transactionSent.setTransactionDate(java.time.LocalDate.now());
        transactionRepository.save(transactionSent);

        Transaction transactionReceived = new Transaction();
        transactionReceived.setOriginAccount(account1);
        transactionReceived.setDestinationAccount(account2);
        transactionReceived.setTransactionType(TransactionType.RECEIVED_TRANSFER);
        transactionReceived.setValue(amount);
        transactionReceived.setTransactionDate(java.time.LocalDate.now());
        transactionRepository.save(transactionReceived);

        accountRepository.update(account1);
        accountRepository.update(account2);
    }

}