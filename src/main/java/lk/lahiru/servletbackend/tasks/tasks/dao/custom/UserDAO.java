package lk.lahiru.servletbackend.tasks.tasks.dao.custom;

import lk.ijse.dep8.tasks.dao.CrudDAO;
import lk.ijse.dep8.tasks.entity.User;

import java.util.Optional;

public interface UserDAO extends CrudDAO<User, String> {

    boolean existsUserByEmailOrId(String emailOrId);

    Optional<User> findUserByIdOrEmail(String userIdOrEmail);

}
