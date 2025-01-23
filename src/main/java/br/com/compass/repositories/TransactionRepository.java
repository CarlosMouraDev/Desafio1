package br.com.compass.repositories;

import br.com.compass.entities.Transaction;
import jakarta.persistence.EntityManager;

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
}