package se.liu.ida.tdp024.account.data.impl.db.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public abstract class EMF {

    private static EntityManagerFactory entityManagerFactory;

    public static EntityManager getEntityManager() {

        synchronized (EMF.class) {
            if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
                entityManagerFactory = Persistence.createEntityManagerFactory("account-dataPU");
            }
        }

        return entityManagerFactory.createEntityManager();
    }

    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
