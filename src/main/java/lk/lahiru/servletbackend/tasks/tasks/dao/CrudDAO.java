package lk.lahiru.servletbackend.tasks.tasks.dao;

import lk.ijse.dep8.tasks.entity.SuperEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface CrudDAO<T extends SuperEntity, ID extends Serializable>
        extends SuperDAO {

    boolean existsById(ID pk);

    T save(T entity);

    void deleteById(ID pk);

    Optional<T> findById(ID pk);

    List<T> findAll();

    long count();
}
