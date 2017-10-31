package edu.tamu.weaver.user.role.service;

import org.springframework.stereotype.Service;

import edu.tamu.weaver.user.model.WeaverRole;
import edu.tamu.weaver.user.model.IRole;

@Service
public abstract class WeaverRoleService implements RoleService {

    @Override
    public IRole valueOf(String role) {
        return WeaverRole.valueOf(WeaverRole.class, role);
    }

}
