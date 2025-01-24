package br.com.compass.repositories;

import br.com.compass.entities.Account;
import br.com.compass.entities.Transaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TransactionRepository {

    private final EntityManager em;

    public TransactionRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Transaction transaction) {
        em.getTransaction().begin();
        em.persist(transaction);
        em.getTransaction().commit();
    }

    public List<Transaction> findTransactionsByAccount(Account account) {
        TypedQuery<Transaction> query = em.createQuery(
                "SELECT t FROM Transaction t WHERE t.originAccount = :account OR t.destinationAccount = :account",
                Transaction.class);
        query.setParameter("account", account);
        return query.getResultList();
    }

}