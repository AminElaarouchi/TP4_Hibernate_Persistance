package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/** SessionFactory unique pour l'application MySQL. Les tests injectent une factory H2. */
public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure();
            override(configuration, "DB_URL", "hibernate.connection.url");
            override(configuration, "DB_USER", "hibernate.connection.username");
            override(configuration, "DB_PASSWORD", "hibernate.connection.password");
            return configuration.buildSessionFactory();
        } catch (RuntimeException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void override(Configuration configuration, String environmentName, String propertyName) {
        String value = System.getenv(environmentName);
        if (value != null) {
            configuration.setProperty(propertyName, value);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void close() {
        SESSION_FACTORY.close();
    }
}
