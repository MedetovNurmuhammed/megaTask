package task.services;

import jakarta.validation.Valid;
import task.dto.request.TaskRequest;
import task.dto.response.DeleteResponse;
import task.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(@Valid TaskRequest request);

    List<TaskResponse> getAllTasks();

    TaskResponse getTaskById(Long id);

    TaskResponse updateTask(Long id, @Valid TaskRequest request);

    DeleteResponse deleteTask(Long id);


}
