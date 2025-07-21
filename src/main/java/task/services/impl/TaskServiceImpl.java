package task.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.dto.request.TaskRequest;
import task.dto.response.DeleteResponse;
import task.dto.response.TaskResponse;
import task.entities.Task;
import task.exceptions.NotFoundException;
import task.mapper.TaskMapper;
import task.repository.TaskRepository;
import task.services.EmailService;
import task.services.TaskService;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final EmailService emailService;

    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskResponse createTask(TaskRequest request) {
        log.info("Создание новой задачи: {}", request.title());

        Task task = taskMapper.toEntity(request);
        Task savedTask = taskRepository.save(task);

        log.info("Задача создана с ID: {}", savedTask.getId());
        try {
            emailService.sendTaskCreatedNotification(savedTask);
        } catch (Exception e) {
            log.error("Ошибка отправки email уведомления: {}", e.getMessage());
        }

        return taskMapper.toResponse(savedTask);
    }

    @Override
    @Cacheable(value = "tasks", key = "'all'")
    public List<TaskResponse> getAllTasks() {
        log.info("Получение всех задач (простое кэширование)");
        List<Task> tasks = taskRepository.findAll();
        log.info("Найдено {} задач", tasks.size());
        return taskMapper.toResponseList(tasks);
    }

    @Override
    @Cacheable(value = "tasks", key = "#id")
    public TaskResponse getTaskById(Long id) {
        log.info("Получение задачи с ID: {} (простое кэширование)", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Задача с ID {} не найдена", id);
                    return new NotFoundException("Задача с ID " + id + " не найдена");
                });

        log.info("Задача найдена: {}", task.getTitle());
        return taskMapper.toResponse(task);
    }

    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskResponse updateTask(Long id, TaskRequest request) {
        log.info("Обновление задачи с ID: {} (очищаем кэш)", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Задача с ID {} не найдена для обновления", id);
                    return new NotFoundException("Задача с ID " + id + " не найдена");
                });

        taskMapper.updateEntity(task, request);
        Task updatedTask = taskRepository.save(task);

        log.info("Задача обновлена: {}", updatedTask.getTitle());
        return taskMapper.toResponse(updatedTask);
    }

    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public DeleteResponse deleteTask(Long id) {
        log.info("Удаление задачи с ID: {} (очищаем кэш)", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Задача с ID {} не найдена для удаления", id);
                    return new NotFoundException("Задача с ID " + id + " не найдена");
                });

        String taskTitle = task.getTitle();
        taskRepository.delete(task);
        log.info("Задача '{}' с ID {} успешно удалена", taskTitle, id);

        return DeleteResponse.success(id);
    }


}