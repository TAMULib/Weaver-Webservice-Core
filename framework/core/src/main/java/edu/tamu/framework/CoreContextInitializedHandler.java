package edu.tamu.framework;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import edu.tamu.framework.model.repo.SymlinkRepo;

@Component
public abstract class CoreContextInitializedHandler implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	public SymlinkRepo symlinkRepo;
	
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
	    		System.out.println("Creating symlink: " + symlink.getPath()+" => "+symlink.getTarget());
	    		try {
					Files.createSymbolicLink( Paths.get(event.getApplicationContext().getResource("classpath:static").getFile().getAbsolutePath() + File.separator +  symlink.getPath()), Paths.get(symlink.target));
				} catch (IOException e) {
					System.out.println("FAILED TO CREATE SYMLINK!!!");				
					e.printStackTrace();
				}
	    	});
    	}
    }
    
    protected abstract void before(ContextRefreshedEvent event);
    
    protected abstract void after(ContextRefreshedEvent event);
	
}
