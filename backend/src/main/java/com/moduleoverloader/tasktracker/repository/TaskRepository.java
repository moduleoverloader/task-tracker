package com.moduleoverloader.tasktracker.repository;

import com.moduleoverloader.tasktracker.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
