package com.taskcolab.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskcolab.task.entity.Task;


import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long id);
}
