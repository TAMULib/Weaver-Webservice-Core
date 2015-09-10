package edu.tamu.framework.model.repo;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Repository;

import edu.tamu.framework.model.Symlink;

@Repository
@ConfigurationProperties("app")
public class SymlinkRepo {

	private Map<String, Symlink> symlinks;

	/**
	 * @return the symlinks
	 */
	public Map<String, Symlink> getSymlinks() {
		return symlinks;
	}

	/**
	 * @param symlinks the symlinks to set
	 */
	public void setSymlinks(Map<String, Symlink> symlinks) {
		this.symlinks = symlinks;
	}
	
}
