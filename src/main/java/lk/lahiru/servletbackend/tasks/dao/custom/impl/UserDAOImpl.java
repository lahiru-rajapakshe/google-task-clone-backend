package lk.lahiru.servletbackend.tasks.dao.custom.impl;

import lk.ijse.dep8.tasks.dao.custom.UserDAO;
import lk.ijse.dep8.tasks.dao.exception.DataAccessException;
import lk.ijse.dep8.tasks.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private final Connection connection;

    public UserDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean existsById(String userId) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT id FROM user WHERE id=?");
            stm.setString(1, userId);
            return stm.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsUserByEmailOrId(String emailOrId){
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT id FROM user WHERE id=? OR email=?");
            stm.setString(1, emailOrId);
            stm.setString(2, emailOrId);
            return stm.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User save(User user) {
        try {
            if (!existsById(user.getId())) {
                PreparedStatement stm = connection.
                        prepareStatement("INSERT INTO user (id, email, password, full_name, profile_pic) VALUES (?, ?, ?, ?, ?)");
                stm.setString(1, user.getId());
                stm.setString(2, user.getEmail());
                stm.setString(3, user.getPassword());
                stm.setString(4, user.getFullName());
                stm.setString(5, user.getProfilePic());
                if (stm.executeUpdate() != 1) {
                    throw new SQLException("Failed to save the user");
                }
            } else {
                PreparedStatement stm = connection.
                        prepareStatement("UPDATE user SET email=?, password=?, full_name=?, profile_pic=? WHERE id=?");
                stm.setString(1, user.getEmail());
                stm.setString(2, user.getPassword());
                stm.setString(3, user.getFullName());
                stm.setString(4, user.getProfilePic());
                stm.setString(5, user.getId());
                if (stm.executeUpdate() != 1) {
                    throw new SQLException("Failed to update the user");
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(String userId) {
        try {
            if (!existsById(userId)){
                throw new DataAccessException("No user found");
            }
            PreparedStatement stm = connection.prepareStatement("DELETE FROM user WHERE id=?");
            stm.setString(1,  userId);
            if (stm.executeUpdate() != 1) {
                throw new SQLException("Failed to delete the user");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findById(String userId) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM user WHERE id=?");
            stm.setString(1,  userId);
            ResultSet rst = stm.executeQuery();
            if (rst.next()){
                return Optional.of(new User(rst.getString("id"),
                        rst.getString("email"),
                        rst.getString("password"),
                        rst.getString("full_name"),
                        rst.getString("profile_pic")));
            }else{
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> findUserByIdOrEmail(String userIdOrEmail) {
        try {
            PreparedStatement stm = connection.
                    prepareStatement("SELECT * FROM user WHERE id=? OR email=?");
            stm.setString(1, userIdOrEmail);
            stm.setString(2, userIdOrEmail);
            ResultSet rst = stm.executeQuery();
            if (rst.next()){
                return Optional.of(new User(rst.getString("id"),
                        rst.getString("email"),
                        rst.getString("password"),
                        rst.getString("full_name"),
                        rst.getString("profile_pic")));
            }else{
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM user");
            List<User> users = new ArrayList<>();
            while (rst.next()) {
                users.add(new User(rst.getString("id"),
                        rst.getString("email"),
                        rst.getString("password"),
                        rst.getString("full_name"),
                        rst.getString("profile_pic")));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT COUNT(id) AS count FROM user");
            if (rst.next()){
                return rst.getLong("count");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
