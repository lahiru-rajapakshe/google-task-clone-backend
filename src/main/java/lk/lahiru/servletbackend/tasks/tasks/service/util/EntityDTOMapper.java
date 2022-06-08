package lk.lahiru.servletbackend.tasks.tasks.service.util;

import lk.ijse.dep8.tasks.dto.TaskDTO;
import lk.ijse.dep8.tasks.dto.TaskListDTO;
import lk.ijse.dep8.tasks.dto.UserDTO;
import lk.ijse.dep8.tasks.entity.Task;
import lk.ijse.dep8.tasks.entity.TaskList;
import lk.ijse.dep8.tasks.entity.User;
import org.modelmapper.ModelMapper;

public class EntityDTOMapper {

    public static UserDTO getUserDTO(User user) {
        ModelMapper mapper = new ModelMapper();
        return mapper.typeMap(User.class, UserDTO.class)
                .addMapping(User::getFullName, UserDTO::setName)
                .addMapping(User::getProfilePic, UserDTO::setPicture)
                .map(user);
    }

    public static TaskListDTO getTaskListDTO(TaskList taskList) {
        ModelMapper mapper = new ModelMapper();
        return mapper.typeMap(TaskList.class, TaskListDTO.class)
                .addMapping(TaskList::getName, TaskListDTO::setTitle)
                .map(taskList);
    }

    public static TaskDTO getTaskDTO(Task task) {
        ModelMapper mapper = new ModelMapper();
        return mapper.typeMap(Task.class, TaskDTO.class)
                .addMapping(Task::getDetails, TaskDTO::setNotes)
                .map(task);
    }

    public static User getUser(UserDTO userDTO) {
        ModelMapper mapper = new ModelMapper();
        return mapper.typeMap(UserDTO.class, User.class)
                .addMapping(UserDTO::getName, User::setFullName)
                .addMapping(UserDTO::getPicture, User::setProfilePic)
                .map(userDTO);
    }

    public static TaskList getTaskList(TaskListDTO taskListDTO) {
        ModelMapper mapper = new ModelMapper();
        return mapper.typeMap(TaskListDTO.class, TaskList.class)
                .addMapping(TaskListDTO::getTitle, TaskList::setName)
                .map(taskListDTO);
    }

    public static Task getTask(TaskDTO taskDTO) {
        ModelMapper mapper = new ModelMapper();
        return mapper.typeMap(TaskDTO.class, Task.class)
                .addMapping(TaskDTO::getNotes, Task::setDetails)
                .map(taskDTO);
    }
}
