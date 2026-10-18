package services;

import dao.IDao;
import dao.MachineDao;
import entities.Machine;
import java.util.List;

public class MachineService implements IDao<Machine> {
    private final MachineDao dao;

    public MachineService() {
        this(new MachineDao());
    }

    public MachineService(MachineDao dao) {
        this.dao = dao;
    }

    @Override
    public boolean create(Machine machine) {
        return dao.create(machine);
    }

    @Override
    public boolean delete(Machine machine) {
        return dao.delete(machine);
    }

    @Override
    public boolean update(Machine machine) {
        return dao.update(machine);
    }

    @Override
    public Machine getById(int id) {
        return dao.getById(id);
    }

    @Override
    public List<Machine> getAll() {
        return dao.getAll();
    }

    public List<Machine> findByMarque(String marque) {
        return dao.findByMarque(marque);
    }
}
