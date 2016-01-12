/* 
 * SymlinkRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.model.repo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import edu.tamu.framework.model.Symlink;

/**
 * Symlink repo.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Repository
public class SymlinkRepo {

	private Map<String, Symlink> symlinks;

	public SymlinkRepo() {
		symlinks = new HashMap<String, Symlink>();
	}

	/**
	 * @return the symlinks
	 */
	public Map<String, Symlink> getSymlinks() {
		return symlinks;
	}

	/**
	 * @param symlinks
	 *            the symlinks to set
	 */
	public void setSymlinks(Map<String, Symlink> symlinks) {
		this.symlinks = symlinks;
	}

}
