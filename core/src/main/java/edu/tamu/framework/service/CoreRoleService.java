/* 
 * CoreRoleService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.service;

import org.springframework.stereotype.Service;

import edu.tamu.framework.enums.CoreRole;
import edu.tamu.framework.model.IRole;

@Service
public abstract class CoreRoleService implements RoleService {

    @Override
    public IRole valueOf(String role) {
        return CoreRole.valueOf(CoreRole.class, role);
    }

}
