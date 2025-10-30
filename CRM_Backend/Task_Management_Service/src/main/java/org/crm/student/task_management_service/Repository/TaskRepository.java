package org.crm.student.task_management_service.Repository;

import org.crm.student.task_management_service.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCandidateFullname(String candidateFullname);
    List<Task> findByStatus(Task.Status status);

}
