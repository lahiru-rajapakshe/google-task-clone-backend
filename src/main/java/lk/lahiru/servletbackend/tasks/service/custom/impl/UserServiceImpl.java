package lk.lahiru.servletbackend.tasks.service.custom.impl;

import lk.ijse.dep8.tasks.dao.DAOFactory;
import lk.ijse.dep8.tasks.dao.custom.UserDAO;
import lk.ijse.dep8.tasks.dto.UserDTO;
import lk.ijse.dep8.tasks.entity.User;
import lk.ijse.dep8.tasks.service.custom.UserService;
import lk.ijse.dep8.tasks.service.exception.FailedExecutionException;
import lk.ijse.dep8.tasks.service.util.EntityDTOMapper;
import lk.ijse.dep8.tasks.service.util.ExecutionContext;
import lk.ijse.dep8.tasks.service.util.JNDIConnectionPool;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.http.Part;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class UserServiceImpl implements UserService {

    private DataSource pool;

    public UserServiceImpl() {
        pool = JNDIConnectionPool.getInstance().getDataSource();
    }

    private final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    public boolean existsUser( String userIdOrEmail)  {
        try (Connection connection = pool.getConnection()) {
            UserDAO userDAO = DAOFactory.getInstance().getDAO(connection, DAOFactory.DAOTypes.USER);
            return userDAO.existsUserByEmailOrId(userIdOrEmail);
        } catch (SQLException t) {
            throw new FailedExecutionException("Failed to check the existence", t);
        }
    }

    public UserDTO registerUser(Part picture,
                                String appLocation,
                                UserDTO user)  {
        Connection connection = null;
        try {
            connection = pool.getConnection();
            connection.setAutoCommit(false);
            user.setId(UUID.randomUUID().toString());

            if (picture != null) {
                user.setPicture(user.getPicture() + user.getId());
            }
            user.setPassword(DigestUtils.sha256Hex(user.getPassword()));

            UserDAO userDAO =  DAOFactory.getInstance().getDAO(connection, DAOFactory.DAOTypes.USER);
            // DTO -> Entity
            User userEntity = EntityDTOMapper.getUser(user);
            User savedUser = userDAO.save(userEntity);
            // Entity -> DTO
            user = EntityDTOMapper.getUserDTO(savedUser);

            if (picture != null) {
                Path path = Paths.get(appLocation, "uploads");
                if (Files.notExists(path)) {
                    Files.createDirectory(path);
                }

                String picturePath = path.resolve(user.getId()).toAbsolutePath().toString();
                picture.write(picturePath);
            }

            connection.commit();
            return user;
        } catch (Throwable t) {
            if (connection != null)
            ExecutionContext.execute(connection::rollback);
            throw new FailedExecutionException("Failed to save the user", t);
        } finally {
            if (connection != null){
                Connection tempConnection = connection;
                ExecutionContext.execute(() -> tempConnection.setAutoCommit(true));
                ExecutionContext.execute(connection::close);
            }
        }
    }

    public UserDTO getUser(String userIdOrEmail)  {
        try (Connection connection = pool.getConnection()) {
            UserDAO userDAO = DAOFactory.getInstance().getDAO(connection, DAOFactory.DAOTypes.USER);
            Optional<User> userWrapper = userDAO.findUserByIdOrEmail(userIdOrEmail);
            return EntityDTOMapper.getUserDTO(userWrapper.orElse(null));
        } catch (SQLException t) {
            throw new FailedExecutionException("Failed to fetch the user", t);
        }
    }

    public void deleteUser(String userId, String appLocation)  {
        try (Connection connection = pool.getConnection()) {
            UserDAO userDAO = DAOFactory.getInstance().getDAO(connection, DAOFactory.DAOTypes.USER);
            userDAO.deleteById(userId);

            new Thread(() -> {
                Path imagePath = Paths.get(appLocation, "uploads",
                        userId);
                try {
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {
                    logger.warning("Failed to delete the image: " + imagePath.toAbsolutePath());
                }
            }).start();
        }catch (SQLException e){
            throw new FailedExecutionException("Failed to delete the user", e);
        }

    }

    public void updateUser(UserDTO user, Part picture,
                           String appLocation) {
        Connection connection = null;
        try {
            connection = pool.getConnection();
            connection.setAutoCommit(false);

            user.setPassword(DigestUtils.sha256Hex(user.getPassword()));

            UserDAO userDAO =  DAOFactory.getInstance().getDAO(connection, DAOFactory.DAOTypes.USER);

            // Fetch the current user
            User userEntity = userDAO.findById(user.getId()).get();

            userEntity.setPassword(user.getPassword());
            userEntity.setFullName(user.getName());
            userEntity.setProfilePic(user.getPicture());

            userDAO.save(userEntity);

            Path path = Paths.get(appLocation, "uploads");
            Path picturePath = path.resolve(user.getId());

            if (picture != null) {
                if (Files.notExists(path)) {
                    Files.createDirectory(path);
                }

                Files.deleteIfExists(picturePath);
                picture.write(picturePath.toAbsolutePath().toString());
            } else {
                Files.deleteIfExists(picturePath);
            }

            connection.commit();
        } catch (Throwable e) {
            if (connection != null)
            ExecutionContext.execute(connection::rollback);
            throw new FailedExecutionException("Failed to update the user", e);
        } finally {
            if (connection != null){
                Connection tempConnection = connection;
                ExecutionContext.execute(() -> tempConnection.setAutoCommit(true));
                ExecutionContext.execute(connection::close);
            }
        }
    }

}
