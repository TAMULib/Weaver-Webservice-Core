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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import edu.tamu.framework.model.repo.SymlinkRepo;

/**
 * Core Context Initialize Handler. Handles initialization for when the app starts or the context is
 * refreshed. Creates symlinks that are populated in the SymlinkRepo. Provides two abstract methods
 * to be implemented.
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Method for event context refreshes.
     * 
     * @param event
     *            The event being triggered.
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
        before(event);
        createSymlinks(event);
        after(event);
    }

    /**
     * Create symlinks.
     * 
     * @param event
     *            The event that is being triggered. Used to get application context.
     */
    private void createSymlinks(ContextRefreshedEvent event) {
        if (symlinkRepo.getSymlinks() != null) {
            symlinkRepo.getSymlinks().values().stream().forEach(symlink -> {
                logger.info("Creating symlink: " + symlink.getPath() + " => " + symlink.getTarget());
                try {
                    Files.createSymbolicLink(Paths.get(event.getApplicationContext().getResource("classpath:static").getFile().getAbsolutePath() + File.separator + symlink.getPath()), Paths.get(symlink.target));
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
