package services;

import dao.DaoException;
import dao.MachineDao;
import dao.SalleDao;
import entities.Machine;
import entities.Salle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceIntegrationTest {
    private static SessionFactory factory;
    private SalleService salles;
    private MachineService machines;

    @BeforeAll
    static void openDatabase() {
        factory = new Configuration().configure("hibernate-test.cfg.xml").buildSessionFactory();
    }

    @AfterAll
    static void closeDatabase() {
        factory.close();
    }

    @BeforeEach
    void cleanDatabase() {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createQuery("delete from Machine").executeUpdate();
            session.createQuery("delete from Salle").executeUpdate();
            tx.commit();
        }
        salles = new SalleService(new SalleDao(factory));
        machines = new MachineService(new MachineDao(factory));
    }

    @Test
    void salleCrud() {
        Salle salle = new Salle("S1", "Salle Informatique");
        assertTrue(salles.create(salle));
        assertTrue(salle.getId() > 0);
        assertEquals("Salle Informatique", salles.getById(salle.getId()).getLibelle());
        assertEquals(1, salles.getAll().size());

        salle.setLibelle("Salle Réseau");
        assertTrue(salles.update(salle));
        assertEquals("Salle Réseau", salles.getById(salle.getId()).getLibelle());

        assertTrue(salles.delete(salle));
        assertNull(salles.getById(salle.getId()));
        assertFalse(salles.delete(salle));
    }

    @Test
    void machineCrudAssociationEtRechercheHql() {
        Salle salle = new Salle("S2", "Atelier");
        salles.create(salle);
        Machine dell = new Machine("M-101", "Dell", salle);
        Machine hp = new Machine("M-102", "HP", salle);
        assertTrue(machines.create(dell));
        assertTrue(machines.create(hp));
        assertEquals(2, machines.getAll().size());
        assertEquals("M-101", machines.findByMarque("Dell").get(0).getRef());

        try (Session session = factory.openSession()) {
            Salle avecMachines = session.createQuery(
                    "select distinct s from Salle s left join fetch s.machines where s.id = :id", Salle.class)
                    .setParameter("id", salle.getId())
                    .uniqueResult();
            assertEquals(2, avecMachines.getMachines().size());
        }

        dell.setMarque("Lenovo");
        assertTrue(machines.update(dell));
        assertEquals(0, machines.findByMarque("Dell").size());
        assertEquals(1, machines.findByMarque("Lenovo").size());
        assertTrue(machines.delete(hp));
        assertEquals(1, machines.getAll().size());
    }

    @Test
    void contrainteUniqueDeclencheRollbackSansInsertionPartielle() {
        salles.create(new Salle("S3", "Première salle"));
        assertThrows(DaoException.class, () -> salles.create(new Salle("S3", "Doublon")));
        assertEquals(1, salles.getAll().size());
    }
}
