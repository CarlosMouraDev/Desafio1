package br.com.compass.repositories;

import br.com.compass.entities.Account;
import br.com.compass.entities.enums.AccountType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

public class AccountRepository {

    private static EntityManager entityManager;

    public AccountRepository(EntityManager entityManager) {
        AccountRepository.entityManager = entityManager;
    }

    public static boolean accountExists(String email, String password) {
        String jpql = "SELECT u FROM Account u WHERE u.email = :email AND u.password = :password";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("email", email);
        query.setParameter("password", password);
        return !query.getResultList().isEmpty();
    }

    public static boolean emailExists(String email) {
        String jpql = "SELECT u FROM Account u WHERE u.email = :email";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("email", email);

        return !query.getResultList().isEmpty();
    }

    public void update(Account account) {
        entityManager.getTransaction().begin();
        entityManager.merge(account);
        entityManager.getTransaction().commit();
    }

    public static Account findByEmail(String email) {
        try {
            TypedQuery<Account> query = entityManager.createQuery(
                    "SELECT u FROM Account u WHERE u.email = :email", Account.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    public static boolean accountCpfTypeExists(String cpf, AccountType accountType) {
        String jpql = "SELECT COUNT(a) FROM Account a WHERE a.cpf = :cpf AND a.accountType = :accountType";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("cpf", cpf);
        query.setParameter("accountType", accountType);

        Long count = query.getSingleResult();
        return count > 0;
    }

}
