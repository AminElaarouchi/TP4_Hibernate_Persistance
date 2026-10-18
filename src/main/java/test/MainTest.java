package test;

import entities.Machine;
import entities.Salle;
import services.MachineService;
import services.SalleService;
import util.HibernateUtil;

/** Démonstration avec MySQL : crée une salle puis une machine. */
public class MainTest {
    public static void main(String[] args) {
        try {
            SalleService salles = new SalleService();
            MachineService machines = new MachineService();
            String suffix = Long.toString(System.currentTimeMillis());
            Salle salle = new Salle("S-" + suffix, "Salle Informatique");
            salles.create(salle);

            Machine machine = new Machine("M-" + suffix, "Dell", salle);
            machines.create(machine);
            System.out.println("Données insérées avec succès : salle " + salle.getId()
                    + ", machine " + machine.getId());
            System.out.println("Machines Dell : " + machines.findByMarque("Dell").size());
        } finally {
            HibernateUtil.close();
        }
    }
}
