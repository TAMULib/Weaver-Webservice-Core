package edu.tamu.weaver.user.role.service;

import org.springframework.stereotype.Service;

import edu.tamu.weaver.user.model.IRole;

@Service
public interface RoleService {

    IRole valueOf(String role);

}
