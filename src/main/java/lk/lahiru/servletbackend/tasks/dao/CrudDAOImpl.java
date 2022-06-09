package lk.lahiru.servletbackend.tasks.dao;

import lk.ijse.dep8.tasks.entity.SuperEntity;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public abstract class CrudDAOImpl<T extends SuperEntity, ID extends Serializable>
        implements CrudDAO<T, ID> {

    private final Class<T> entityClsObj;
    protected Session session;

    public CrudDAOImpl() {
        entityClsObj = (Class<T>) (((ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]);
    }

    @Override
    public boolean existsById(ID pk) {
        return findById(pk).isPresent();
    }

    @Override
    public T save(T entity) {
        session.save(entity);
        return entity;
    }

    @Override
    public void deleteById(ID pk) {
        session.delete(session.load(entityClsObj, pk));
    }

    @Override
    public Optional<T> findById(ID pk) {
        T entity = session.get(entityClsObj, pk);
        return (entity == null) ? Optional.empty() : Optional.of(entity);
    }

    @Override
    public List<T> findAll() {
        return session.createQuery("FROM " + entityClsObj.getName(), entityClsObj).list();
    }

    @Override
    public long count() {
        return session.createQuery("SELECT COUNT(entity) FROM "+ entityClsObj.getName() +" entity", Long.class).uniqueResult();
    }
}
