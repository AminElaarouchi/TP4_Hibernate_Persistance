package dao;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/** Implémentation CRUD et transactions partagées par les DAO des entités. */
public abstract class AbstractHibernateDao<T> implements IDao<T> {
    private final Class<T> entityClass;
    private final SessionFactory sessionFactory;
    private final ToIntFunction<T> idOf;

    protected AbstractHibernateDao(Class<T> entityClass, SessionFactory sessionFactory, ToIntFunction<T> idOf) {
        this.entityClass = entityClass;
        this.sessionFactory = sessionFactory;
        this.idOf = idOf;
    }

    protected SessionFactory sessionFactory() {
        return sessionFactory;
    }

    @Override
    public boolean create(T object) {
        return write(session -> {
            session.persist(object);
            return true;
        });
    }

    @Override
    public boolean delete(T object) {
        return write(session -> {
            T managed = session.get(entityClass, idOf.applyAsInt(object));
            if (managed == null) {
                return false;
            }
            session.delete(managed);
            return true;
        });
    }

    @Override
    public boolean update(T object) {
        return write(session -> {
            if (session.get(entityClass, idOf.applyAsInt(object)) == null) {
                return false;
            }
            session.merge(object);
            return true;
        });
    }

    @Override
    public T getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(entityClass, id);
        }
    }

    @Override
    public List<T> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from " + entityClass.getSimpleName(), entityClass).list();
        }
    }

    protected boolean write(Function<Session, Boolean> operation) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                boolean result = operation.apply(session);
                transaction.commit();
                return result;
            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    try {
                        transaction.rollback();
                    } catch (RuntimeException rollbackError) {
                        e.addSuppressed(rollbackError);
                    }
                }
                throw new DaoException("Erreur transactionnelle : " + e.getMessage(), e);
            }
        }
    }
}
