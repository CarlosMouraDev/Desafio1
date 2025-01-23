package br.com.compass.services;

import br.com.compass.entities.Account;
import br.com.compass.repositories.AccountRepository;
import br.com.compass.services.exceptions.BankServiceException;

public class BankService {
    private AccountRepository accountRepository;

    public BankService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void deposit(String email, float amount)  {
        Account account = accountRepository.findByEmail(email);
        account.increaseBalance(amount);
        accountRepository.update(account);
    }

    public void withdraw(String email, float amount)  throws BankServiceException {
        Account account = accountRepository.findByEmail(email);
        if (account.getBalance() < amount) {
            throw new BankServiceException("Insufficient funds.");
        }

        account.decreaseBalance(amount);
        accountRepository.update(account);
    }
}