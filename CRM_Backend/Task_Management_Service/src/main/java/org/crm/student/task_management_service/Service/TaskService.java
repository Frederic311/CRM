package org.crm.student.task_management_service.Service;

import org.crm.student.task_management_service.Repository.TaskRepository;
import org.crm.student.task_management_service.model.EmailNotificationRequest;
import org.crm.student.task_management_service.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CandidateClient candidateClient;
    private final UserClient userClient;

    @Autowired
    private RestTemplate restTemplate;

    private static final String NOTIFICATION_SERVICE_URL = "http://localhost:8085/api/notifications/task-assignment";
    private static final String NOTIFICATION_COMPLETION_URL = "http://localhost:8085/api/notifications/task-completion";
    private static final String USER_EMAIL_SERVICE_URL = "http://localhost:8060/api/v1/auth/";


    @Autowired
    public TaskService(TaskRepository taskRepository, CandidateClient candidateClient, UserClient userClient) {
        this.taskRepository = taskRepository;
        this.candidateClient = candidateClient;
        this.userClient = userClient;
    }

    public Task createTask(Task task) {


        // Validate assignedTo user
        boolean isUserValid = userClient.validateUser(task.getAssignedTo());

        if (!isUserValid) {
            throw new IllegalArgumentException("Invalid assignedTo: " + task.getAssignedTo());
        }

        if (!"no association".equals(task.getCandidateFullname())) {
            boolean isCandidateValid = candidateClient.validateCandidate(task.getCandidateFullname());

            if (!isCandidateValid) {
                throw new IllegalArgumentException("Invalid candidateName: " + task.getCandidateFullname());
            }
        }

        // Save the task
        Task savedTask = taskRepository.save(task);
        String assignedToEmail = getAssignedToEmail(task.getAssignedTo());
        task.setAssignedToEmail(assignedToEmail);

        // Send email notification
        sendEmailNotification(savedTask);

        return savedTask;
    }

    private String getAssignedToEmail(String assignedTo) {
        // Construct the URL to fetch the email
        String url = USER_EMAIL_SERVICE_URL + assignedTo + "/email";
        return restTemplate.getForObject(url, String.class);
    }

    private void sendEmailNotification(Task task) {
        // Prepare the email details
        EmailNotificationRequest emailRequest = new EmailNotificationRequest();
        emailRequest.setTo(task.getAssignedToEmail());
        emailRequest.setSubject("New Task Assigned: " + task.getAssignedTo());
        emailRequest.setMessage("Hello, a new task has been assigned to you. Details:\n\n" +
                "Task Name: " + task.getAssignedTo() + "\n" +
                "Description: " + task.getDescription());

        // Send the request to the email service
        restTemplate.postForObject(NOTIFICATION_SERVICE_URL, emailRequest, String.class);
    }
    private void sendCompletionEmailNotification(Task task) {
        // Prepare the email details
        EmailNotificationRequest emailRequest = new EmailNotificationRequest();
        emailRequest.setTo(task.getAssignedToEmail());
        emailRequest.setSubject("Completion Task: " + task.getAssignedTo());
        emailRequest.setMessage("Hello, a task has been Completed. Details:\n\n" +
                "Task Name: " + task.getAssignedTo() + "\n" +
                "Description: " + task.getDescription());

        // Send the request to the email service
        restTemplate.postForObject(NOTIFICATION_COMPLETION_URL, emailRequest, String.class);
    }


    public List<Task> getTasksByCandidateId(String candidateFullname) {
        return taskRepository.findByCandidateFullname(candidateFullname);
    }

    public List<Task> getTasksByStatus(Task.Status status) {
        return taskRepository.findByStatus(status);
    }

    public Optional<Task> updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            if (updatedTask.isCompleted() && LocalDate.now().isAfter(task.getDeadline())) {
                throw new IllegalStateException("Task cannot be marked as completed because the deadline has passed.");
            }

            // Update task fields
            task.setDescription(updatedTask.getDescription());
            task.setType(updatedTask.getType());
            task.setDeadline(updatedTask.getDeadline());
            task.setPriority(updatedTask.getPriority());
            task.setAssignedTo(updatedTask.getAssignedTo());
            task.setStatus(updatedTask.getStatus());
            task.setCompleted(updatedTask.isCompleted());

            return taskRepository.save(task);
        });
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> findTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public Optional<Task> markTaskAsCompleted(Long id) {
        return taskRepository.findById(id).map(task -> {
            if (task.getDeadline().isBefore(LocalDate.now())) {
                throw new IllegalStateException("Cannot mark task as completed; deadline has passed.");
            }

            task.setStatus(Task.Status.COMPLETED);
            task.setCompleted(true);

            String assignedToEmail = getAssignedToEmail(task.getAssignedTo());
            task.setAssignedToEmail(assignedToEmail);

            // Send completion email notification
            sendCompletionEmailNotification(task);

            return taskRepository.save(task);
        });
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
