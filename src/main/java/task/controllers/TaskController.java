package task.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task.dto.request.TaskRequest;
import task.dto.response.DeleteResponse;
import task.dto.response.SuccessResponse;
import task.dto.response.TaskResponse;
import task.services.TaskService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<SuccessResponse> createTask(@Valid @RequestBody TaskRequest request) {
        log.info("POST /api/tasks - Создание новой задачи: {}", request.title());
        TaskResponse createdTask = taskService.createTask(request);
        log.info("Задача создана с ID: {}", createdTask.getId());

        SuccessResponse response = SuccessResponse.created(
                "Задача успешно создана",
                createdTask
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        log.info("GET /api/tasks - Получение всех задач");
        List<TaskResponse> tasks = taskService.getAllTasks();
        log.info("Возвращено {} задач", tasks.size());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        log.info("GET /api/tasks/{} - Получение задачи по ID", id);
        TaskResponse task = taskService.getTaskById(id);
        log.info("Задача найдена: {}", task.getId());
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse> updateTask(@PathVariable Long id,
                                                   @Valid @RequestBody TaskRequest request) {
        log.info("PUT /api/tasks/{} - Обновление задачи", id);
        TaskResponse updatedTask = taskService.updateTask(id, request);
        log.info("Задача обновлена: {}", updatedTask.getId());
        SuccessResponse response = SuccessResponse.created(
                "Задача успешно обновлено!",
                updatedTask
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> deleteTask(@PathVariable Long id) {
        log.info("DELETE /api/tasks/{} - Удаление задачи", id);
        DeleteResponse deleteResponse = taskService.deleteTask(id);
        log.info("Задача с ID {} успешно удалена", id);
        return ResponseEntity.ok(deleteResponse);
    }


}