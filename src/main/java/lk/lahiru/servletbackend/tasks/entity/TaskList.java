package lk.lahiru.servletbackend.tasks.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskList implements SuperEntity {
    private int id;
    private String name;
    private String userId;

}
