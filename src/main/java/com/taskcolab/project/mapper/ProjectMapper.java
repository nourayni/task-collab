package com.taskcolab.project.mapper;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.taskcolab.project.dto.ProjectDTO;
import com.taskcolab.project.dto.ProjectMemberDTO;
import com.taskcolab.project.entity.Project;
import com.taskcolab.project.entity.ProjectMember;
import com.taskcolab.user.entity.User;
import com.taskcolab.user.mapper.UserMapper;
import com.taskcolab.user.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service @AllArgsConstructor
public class ProjectMapper {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public ProjectMemberDTO mapperProjectMemberToProjectMemberDTO(ProjectMember projectMember){
        ProjectMemberDTO pm = new ProjectMemberDTO();
        pm.setId(projectMember.getId());
        // pm.setProjectDTO(mapperProjectToProjectDTO(projectMember.getProject()));
        pm.setUserDTO(userMapper.userToUserDTO(projectMember.getUser()));
        pm.setRole(projectMember.getRole());
        return pm;
    }

    public ProjectDTO mapperProjectToProjectDTO(Project project){
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName(project.getName());
        projectDTO.setDescription(project.getDescription());
        projectDTO.setOwnerId(project.getOwner().getId());
        projectDTO.setArchived(project.isArchived());
        projectDTO.setId(project.getId());

        if(project.getMembers()==null){
            projectDTO.setMembers(null);
        }

        projectDTO.setMembers(project.getMembers().stream().map(member -> mapperProjectMemberToProjectMemberDTO(member)).collect(Collectors.toList()));
        return projectDTO;
    }
}
