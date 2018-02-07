/* 
 * RoleService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.service;

import org.springframework.stereotype.Service;

import edu.tamu.weaver.model.IRole;

@Service
public interface RoleService {

    IRole valueOf(String role);

}
