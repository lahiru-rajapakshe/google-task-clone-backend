package lk.lahiru.servletbackend.tasks.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Task implements SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String details;
    private int position;
    @Enumerated(EnumType.STRING)
    private Status status;
    @JoinColumn(name = "task_list_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private TaskList taskList;

    public enum Status{
        completed, needsAction
    }

    public Task(String title, String details, int position, Status status, TaskList taskList) {
        this.title = title;
        this.details = details;
        this.position = position;
        this.status = status;
        this.taskList = taskList;
    }
}
