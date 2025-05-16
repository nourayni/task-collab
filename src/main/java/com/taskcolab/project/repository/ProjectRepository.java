package com.taskcolab.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskcolab.project.entity.Project;



public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerEmail(String email);
}
