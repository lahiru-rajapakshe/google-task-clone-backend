package lk.lahiru.servletbackend.tasks.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class User implements SuperEntity {
    @Id
    private String id;
    private String email;
    private String password;
    private String fullName;
    private String profilePic;
}
