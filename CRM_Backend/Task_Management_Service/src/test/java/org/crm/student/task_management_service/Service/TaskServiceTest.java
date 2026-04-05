package org.crm.student.task_management_service.Service;

import org.crm.student.task_management_service.Repository.TaskRepository;
import org.crm.student.task_management_service.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CandidateClient candidateClient;

    @Mock
    private UserClient userClient;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(taskService, "restTemplate", restTemplate);
    }

    @Test
    void createTask_shouldRejectInvalidAssignedUser() {
        Task task = new Task();
        task.setAssignedTo("unknown.user");
        task.setCandidateFullname("no association");

        when(userClient.validateUser("unknown.user")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));

        assertEquals("Invalid assignedTo: unknown.user", exception.getMessage());
        verify(taskRepository, never()).save(task);
    }

    @Test
    void markTaskAsCompleted_shouldRejectWhenDeadlinePassed() {
        Task task = new Task();
        task.setId(7L);
        task.setDeadline(LocalDate.now().minusDays(1));

        when(taskRepository.findById(7L)).thenReturn(Optional.of(task));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> taskService.markTaskAsCompleted(7L));

        assertEquals("Cannot mark task as completed; deadline has passed.", exception.getMessage());
        verify(taskRepository, never()).save(task);
    }
}
