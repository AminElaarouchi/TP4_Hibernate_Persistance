package services;

import dao.IDao;
import dao.SalleDao;
import entities.Salle;
import java.util.List;

public class SalleService implements IDao<Salle> {
    private final IDao<Salle> dao;

    public SalleService() {
        this(new SalleDao());
    }

    public SalleService(IDao<Salle> dao) {
        this.dao = dao;
    }

    @Override
    public boolean create(Salle salle) {
        return dao.create(salle);
    }

    @Override
    public boolean delete(Salle salle) {
        return dao.delete(salle);
    }

    @Override
    public boolean update(Salle salle) {
        return dao.update(salle);
    }

    @Override
    public Salle getById(int id) {
        return dao.getById(id);
    }

    @Override
    public List<Salle> getAll() {
        return dao.getAll();
    }
}
