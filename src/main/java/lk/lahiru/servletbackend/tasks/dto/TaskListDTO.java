package lk.lahiru.servletbackend.tasks.dto;

import jakarta.json.bind.annotation.JsonbTransient;

import java.io.Serializable;

public class TaskListDTO implements Serializable {
    private Integer id;
    private String title;
    @JsonbTransient
    private String userId;

    public TaskListDTO() {

    }

    public TaskListDTO(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public TaskListDTO(Integer id, String title, String userId) {
        this.id = id;
        this.title = title;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "TaskListDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
