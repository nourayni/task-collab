package com.taskcolab.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskcolab.project.entity.Project;



public interface ProjectRepository extends JpaRepository<Project, Long> {
 
}
