package task.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task.dto.request.TaskRequest;
import task.dto.response.DeleteResponse;
import task.dto.response.TaskResponse;
import task.entities.Task;
import task.exceptions.NotFoundException;
import task.mapper.TaskMapper;
import task.repository.TaskRepository;
import task.services.EmailService;
import task.services.impl.TaskServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task sampleTask;
    private TaskResponse sampleTaskResponse;
    private TaskRequest sampleTaskRequest;
    private TaskRequest sampleTaskUpdateRequest;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Тестовая задача");
        sampleTask.setDescription("Описание тестовой задачи");
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setUpdatedAt(LocalDateTime.now());

        sampleTaskResponse = TaskResponse.builder()
                .id(1L)
                .title("Тестовая задача")
                .description("Описание тестовой задачи")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleTaskRequest = TaskRequest.builder()
                .title("Новая задача")
                .description("Описание новой задачи")
                .build();

        sampleTaskUpdateRequest = TaskRequest.builder()
                .title("Обновленная задача")
                .description("Обновленное описание")
                .build();
    }

    @Test
    void createTask_ShouldCreateTaskSuccessfully() {
        when(taskMapper.toEntity(sampleTaskRequest)).thenReturn(sampleTask);
        when(taskRepository.save(sampleTask)).thenReturn(sampleTask);
        when(taskMapper.toResponse(sampleTask)).thenReturn(sampleTaskResponse);
        doNothing().when(emailService).sendTaskCreatedNotification(sampleTask);

        var result = taskService.createTask(sampleTaskRequest);

        assertNotNull(result);
        assertEquals(sampleTaskResponse.getId(), result.getId());
        assertEquals(sampleTaskResponse.getTitle(), result.getTitle());
        verify(taskMapper).toEntity(sampleTaskRequest);
        verify(taskRepository).save(sampleTask);
        verify(taskMapper).toResponse(sampleTask);
        verify(emailService).sendTaskCreatedNotification(sampleTask);
    }

    @Test
    void createTask_ShouldHandleEmailException() {
        when(taskMapper.toEntity(sampleTaskRequest)).thenReturn(sampleTask);
        when(taskRepository.save(sampleTask)).thenReturn(sampleTask);
        when(taskMapper.toResponse(sampleTask)).thenReturn(sampleTaskResponse);
        doThrow(new RuntimeException("Email error")).when(emailService).sendTaskCreatedNotification(sampleTask);

        var result = taskService.createTask(sampleTaskRequest);

        assertNotNull(result);
        assertEquals(sampleTaskResponse.getId(), result.getId());
        verify(emailService).sendTaskCreatedNotification(sampleTask);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        List<Task> tasks = Arrays.asList(sampleTask);
        List<TaskResponse> taskResponses = Arrays.asList(sampleTaskResponse);
        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.toResponseList(tasks)).thenReturn(taskResponses);

        List<TaskResponse> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleTaskResponse.getTitle(), result.get(0).getTitle());
        verify(taskRepository).findAll();
        verify(taskMapper).toResponseList(tasks);
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskMapper.toResponse(sampleTask)).thenReturn(sampleTaskResponse);

        var result = taskService.getTaskById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(sampleTaskResponse.getId(), result.getId());
        assertEquals(sampleTaskResponse.getTitle(), result.getTitle());
        verify(taskRepository).findById(1L);
        verify(taskMapper).toResponse(sampleTask);
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldThrowException() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            taskService.getTaskById(999L);
        });

        assertEquals("Задача с ID 999 не найдена", exception.getMessage());
        verify(taskRepository).findById(999L);
        verify(taskMapper, never()).toResponse(any());
    }

    @Test
    void updateTask_WhenTaskExists_ShouldUpdateTask() {
        var updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Обновленная задача");
        updatedTask.setDescription("Обновленное описание");

        TaskResponse updatedTaskResponse = TaskResponse.builder()
                .id(1L)
                .title("Обновленная задача")
                .description("Обновленное описание")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskMapper).updateEntity(sampleTask, sampleTaskUpdateRequest);
        when(taskRepository.save(sampleTask)).thenReturn(updatedTask);
        when(taskMapper.toResponse(updatedTask)).thenReturn(updatedTaskResponse);

        TaskResponse result = taskService.updateTask(1L, sampleTaskUpdateRequest);

        assertNotNull(result);
        assertEquals(updatedTaskResponse.getTitle(), result.getTitle());
        verify(taskRepository).findById(1L);
        verify(taskMapper).updateEntity(sampleTask, sampleTaskUpdateRequest);
        verify(taskRepository).save(sampleTask);
        verify(taskMapper).toResponse(updatedTask);
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldDeleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskRepository).delete(sampleTask);
        DeleteResponse result = taskService.deleteTask(1L);

        assertNotNull(result);
        assertEquals("Задача успешно удалена", result.getMessage());
        assertEquals(1L, result.getDeletedId());
        assertNotNull(result.getTimestamp());
        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(sampleTask);
    }

    @Test
    void deleteTask_WhenTaskDoesNotExist_ShouldThrowException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            taskService.deleteTask(999L);
        });

        assertEquals("Задача с ID 999 не найдена", exception.getMessage());
        verify(taskRepository).findById(999L);
        verify(taskRepository, never()).delete(any());
    }
}