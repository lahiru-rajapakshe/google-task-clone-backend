package lk.lahiru.servletbackend.tasks.tasks.security;

import lk.ijse.dep8.tasks.dto.UserDTO;

public class SecurityContextHolder {

    private static volatile ThreadLocal<UserDTO> principal = new ThreadLocal<>();

    public static void setPrincipal(UserDTO user){
        principal.set(user);
    }

    public static UserDTO getPrincipal(){
        return principal.get();
    }
}
