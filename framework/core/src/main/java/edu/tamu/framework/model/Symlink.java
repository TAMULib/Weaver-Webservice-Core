package edu.tamu.framework.model;

public class Symlink {

	public String path;
	public String target;
	
	public Symlink() {
		// TODO Auto-generated constructor stub
	}
	
	public Symlink(String path, String target) {
		this.path = path;
		this.target = target;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

}
