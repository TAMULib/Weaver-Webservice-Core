/* 
 * RoleService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.service;

import org.springframework.stereotype.Service;

import edu.tamu.framework.model.IRole;

@Service
public interface RoleService {

    IRole valueOf(String role);

}
