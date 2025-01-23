package br.com.compass.services;

import br.com.compass.entities.Account;
import br.com.compass.entities.Transaction;
import br.com.compass.entities.enums.TransactionType;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.repositories.TransactionRepository;
import br.com.compass.services.exceptions.BankServiceException;

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
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setValue(amount);
        transaction.setTransactionDate(java.time.LocalDate.now());
        transactionRepository.save(transaction);
    }

}