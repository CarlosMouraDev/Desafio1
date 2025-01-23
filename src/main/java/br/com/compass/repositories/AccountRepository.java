package br.com.compass.repositories;

import br.com.compass.entities.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

public class AccountRepository {

    private static EntityManager entityManager;

    public AccountRepository(EntityManager entityManager) {
        AccountRepository.entityManager = entityManager;
    }

    public static boolean accountExists(String email, String senha) {
        String jpql = "SELECT u FROM Account u WHERE u.email = :email AND u.password = :senha";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("email", email);
        query.setParameter("senha", senha);

        return !query.getResultList().isEmpty();
    }

    public static boolean cpfExists(String cpf) {
        String jpql = "SELECT u FROM Account u WHERE u.cpf = :cpf";
        Query query = entityManager.createQuery(jpql);
        query.setParameter("cpf", cpf);

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

    public Account findByEmail(String email) {
        try {
            TypedQuery<Account> query = entityManager.createQuery(
                    "SELECT u FROM Account u WHERE u.email = :email", Account.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }
}
