package lk.lahiru.servletbackend.tasks.service.custom;

import lk.ijse.dep8.tasks.dto.UserDTO;
import lk.ijse.dep8.tasks.service.SuperService;

import javax.servlet.http.Part;

public interface UserService extends SuperService {

    boolean existsUser(String userIdOrEmail);

    UserDTO registerUser(Part picture,
                         String appLocation,
                         UserDTO user);

    UserDTO getUser(String userIdOrEmail);

    void deleteUser(String userId, String appLocation);

    void updateUser(UserDTO user, Part picture,
                    String appLocation);
}
