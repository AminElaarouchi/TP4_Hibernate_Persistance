package dao;

import entities.Salle;
import org.hibernate.SessionFactory;
import util.HibernateUtil;

public class SalleDao extends AbstractHibernateDao<Salle> {
    public SalleDao() {
        this(HibernateUtil.getSessionFactory());
    }

    public SalleDao(SessionFactory sessionFactory) {
        super(Salle.class, sessionFactory, Salle::getId);
    }
}
