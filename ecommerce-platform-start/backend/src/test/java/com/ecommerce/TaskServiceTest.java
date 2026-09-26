package com.ecommerce;

import com.ecommerce.dto.CreateTaskRequest;
import com.ecommerce.dto.TaskResponse;
import com.ecommerce.dto.UpdateTaskRequest;
import com.ecommerce.exception.ApiException;
import com.ecommerce.messaging.TaskCreatedEvent;
import com.ecommerce.model.Task;
import com.ecommerce.model.TaskStatus;
import com.ecommerce.model.User;
import com.ecommerce.repository.TaskRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Test unitar pentru TaskService: fara Spring, fara baza de date - repository-urile sunt mock-uri (Mockito). */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationEventPublisher events;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createSavesTaskWithDefaultStatusAndPublishesEvent() {
        User owner = new User();
        owner.setEmail("ana@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = taskService.create(1L, new CreateTaskRequest("  Titlu  ", "Descriere", null));

        assertEquals("Titlu", response.title());          // titlul e curatat de spatii
        assertEquals(TaskStatus.TODO, response.status()); // status implicit
        verify(events).publishEvent(any(TaskCreatedEvent.class));
    }

    @Test
    void updateChangesFieldsOfOwnTask() {
        Task task = new Task();
        task.setTitle("Vechi");
        task.setStatus(TaskStatus.TODO);
        when(taskRepository.findByIdAndOwnerId(5L, 1L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.update(1L, 5L, new UpdateTaskRequest("Nou", "Desc", TaskStatus.DONE));

        assertEquals("Nou", response.title());
        assertEquals(TaskStatus.DONE, response.status());
    }

    @Test
    void deleteOfSomeoneElsesTaskThrowsNotFoundAndDeletesNothing() {
        // repository-ul nu gaseste task-ul 5 printre task-urile userului 1 -> apartine altcuiva sau nu exista
        when(taskRepository.findByIdAndOwnerId(5L, 1L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> taskService.delete(1L, 5L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(taskRepository, never()).delete(any(Task.class));
    }
}
