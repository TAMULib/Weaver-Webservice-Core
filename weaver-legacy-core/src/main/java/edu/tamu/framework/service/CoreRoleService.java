/* 
 * CoreRoleService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.service;

import org.springframework.stereotype.Service;

import edu.tamu.weaver.enums.CoreRole;
import edu.tamu.weaver.model.IRole;

@Service
public abstract class CoreRoleService implements RoleService {

    @Override
    public IRole valueOf(String role) {
        return CoreRole.valueOf(CoreRole.class, role);
    }

}
