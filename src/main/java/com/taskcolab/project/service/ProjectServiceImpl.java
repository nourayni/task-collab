package com.taskcolab.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskcolab.common.ResourceNotFoundException;
import com.taskcolab.project.dto.ProjectDTO;
import com.taskcolab.project.dto.ProjectRequest;
import com.taskcolab.project.dto.ProjectStatsDTO;
import com.taskcolab.project.entity.Project;
import com.taskcolab.project.entity.ProjectMember;
import com.taskcolab.project.mapper.ProjectMapper;
import com.taskcolab.project.repository.ProjectRepository;
import com.taskcolab.task.entity.Task;
import com.taskcolab.task.repository.TaskRepository;
import com.taskcolab.user.entity.User;
import com.taskcolab.user.repository.UserRepository;

@Service @Transactional
public class ProjectServiceImpl implements ProjectService {
    private final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository,UserRepository userRepository,
                                                                    TaskRepository taskRepository, ProjectMapper projectMapper){
        this.projectRepository=projectRepository;
        this.taskRepository= taskRepository;
        this.userRepository= userRepository;
        this.projectMapper =  projectMapper;
    }


    @Override
    public ProjectDTO createProject(ProjectRequest projectRequest, String userEmail) {
        // chercher le proprietaire du projet s'il existe
        User owner = userRepository.findByEmail(userEmail).orElseThrow(()->
                    new ResourceNotFoundException("utilisateur non trouve avec email: "+ userEmail));
        logger.info("nom du projet: {}, decription: {}",projectRequest.getName(),projectRequest.getDescription());
        Project project = new Project();
        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setOwner(owner);
        project.setArchived(false);
        
        Project projectSaved = projectRepository.save(project);
        return projectMapper.mapperProjectToProjectDTO(projectSaved);
    }

    @Override
    public ProjectDTO getProject(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("projet non trouve a l'id: "+ id));

        return projectMapper.mapperProjectToProjectDTO(project);
    }

    @Override
    public List<ProjectDTO> getAlProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream().map(project -> projectMapper.mapperProjectToProjectDTO(project)).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {

        Project project = projectRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("projet non trouve a l'id: "+ id));

        logger.info("modifier le projet a l'id: {}", project.getId());

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setArchived(projectDTO.isArchived());

        Project updateProject = projectRepository.save(project);
        return projectMapper.mapperProjectToProjectDTO(updateProject);
    }

    @Override
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ce projet n'existe pas: "+ id);
        }
        projectRepository.deleteById(id);
    }

    @Override
    public void addMember(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new ResourceNotFoundException("projet non trouve a l'id: "+ projectId));
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user non trouve a l id: "+ userId));

        boolean alreadyMember = project.getMembers().stream().anyMatch(member ->member.getUser().getId().equals(userId));
        if (alreadyMember) {
            throw new IllegalStateException("l'utilisateur est deja membre");
        }

        ProjectMember pm = new ProjectMember();
        pm.setProject(project);
        pm.setUser(user);
        pm.setRole("MEMBER");
        project.getMembers().add(pm);
        projectRepository.save(project);

    }

    @Override
    public List<ProjectDTO> getUserProject(String email) {
        // List<Project> userProjects = projectRepository.findByUserEmail(email);
        return projectRepository.findByOwnerEmail(email).stream().map(project ->
                                            projectMapper.mapperProjectToProjectDTO(project)).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO archiveProject(Long id, boolean archive) {
        Project project = projectRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("projet non trouve"));
        project.setArchived(archive);
        projectRepository.save(project);
        return projectMapper.mapperProjectToProjectDTO(project);
    }

    @Override
    public ProjectStatsDTO getProjectStats(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("project non trouve"));
        List<Task> tasks = taskRepository.findByProjectId(id);

        ProjectStatsDTO state = new ProjectStatsDTO();
        state.setProjectId(project.getId());
        state.setProjectTitle(project.getName());
        state.setTotalTasks(tasks.size());
        state.setDoneTasks(tasks.stream().filter(task -> "DONE".equals(task.getStatus())).count());
        state.setInProgressTasks(tasks.stream().filter(task -> "IN_PROGRESS".equals(task.getStatus())).count());
        state.setTodoTasks(tasks.stream().filter(task -> "TODO".equals(task.getStatus())).count());
        return state;
    }

    
    
}
