/* 
 * CoreContextInitializedHandler.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import edu.tamu.framework.model.repo.SymlinkRepo;

/** 
 * Context Initialize Handler
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Component
public abstract class CoreContextInitializedHandler implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private SymlinkRepo symlinkRepo;
	
	private static final Logger logger = Logger.getLogger(CoreContextInitializedHandler.class);
	
    /**
     * Method for event context refreshes.
     * 
     * @param		event		ContextRefreshedEvent
     * 
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
    		before(event);
        	createSymlinks(event);
        	after(event);
    }
    
    private void createSymlinks(ContextRefreshedEvent event) {
    	if(symlinkRepo.getSymlinks() != null) {
	    	symlinkRepo.getSymlinks().values().stream().forEach(symlink->{
	    		logger.info("Creating symlink: " + symlink.getPath()+" => "+symlink.getTarget());
	    		try {
					Files.createSymbolicLink( Paths.get(event.getApplicationContext().getResource("classpath:static").getFile().getAbsolutePath() + File.separator +  symlink.getPath()), Paths.get(symlink.target));
				} catch (IOException e) {
					logger.error("Failed to create symlink. " + e.getMessage());				
					e.printStackTrace();
				}
	    	});
    	}
    }
    
    protected abstract void before(ContextRefreshedEvent event);
    
    protected abstract void after(ContextRefreshedEvent event);
	
}
