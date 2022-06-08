package lk.lahiru.servletbackend.tasks.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Task implements SuperEntity {
    private int id;
    private String title;
    private String details;
    private int position;
    private Status status;
    private int taskListId;

    public enum Status{
        completed, needsAction
    }
}
