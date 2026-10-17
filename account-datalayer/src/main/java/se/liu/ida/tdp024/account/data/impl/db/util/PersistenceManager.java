package se.liu.ida.tdp024.account.data.impl.db.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class PersistenceManager {

    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("accountPU");

    private PersistenceManager() {}

    public static EntityManager createEntityManager() {
        return EMF.createEntityManager();
    }

    public static void shutdown() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }
}