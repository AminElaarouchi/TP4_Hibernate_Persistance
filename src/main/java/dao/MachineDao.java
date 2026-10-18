package dao;

import entities.Machine;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import util.HibernateUtil;

public class MachineDao extends AbstractHibernateDao<Machine> {
    public MachineDao() {
        this(HibernateUtil.getSessionFactory());
    }

    public MachineDao(SessionFactory sessionFactory) {
        super(Machine.class, sessionFactory, Machine::getId);
    }

    public List<Machine> findByMarque(String marque) {
        try (Session session = sessionFactory().openSession()) {
            return session.createQuery("from Machine m where m.marque = :marque", Machine.class)
                    .setParameter("marque", marque)
                    .list();
        }
    }
}
