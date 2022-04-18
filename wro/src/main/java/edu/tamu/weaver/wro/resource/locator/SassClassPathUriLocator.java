package edu.tamu.weaver.wro.resource.locator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import ro.isdc.wro.model.resource.locator.UriLocator;
import ro.isdc.wro.model.resource.locator.support.LocatorProvider;

/**
 * Custom loader to support SASS files containing imports and loaded by classpath
 *
 * Adapted from WRO native SassUriLocator: https://github.com/wro4j/wro4j/pull/1048/files
 *
 * @author Jason Savell
 */
public class SassClassPathUriLocator implements UriLocator {

    private static final Logger LOG = LoggerFactory.getLogger(SassClassPathUriLocator.class);

    /**
     * Alias used to register this locator with {@link LocatorProvider}.
     */
    public static final String ALIAS = "sassClassPathUri";

    private ResourcePatternResolver resourcePatternResolver;

    public SassClassPathUriLocator(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final String url) {
        boolean accepted = false;
        if (url != null) {
            final String extension = FilenameUtils.getExtension(url);
            // scss file have either no extension or scss
            // maybe check for the "_"?
            if ("".equals(extension) || "scss".equals(extension)) {
                accepted = getScssFile(url).isPresent();
            }
        }
        if (!accepted) {
            LOG.debug("Possible scss file not found {}", url);
        }
        return accepted;
    }

    private Optional<File> getScssFile(String url) {

        Optional<File> file = Optional.empty();

        if (!url.endsWith(".scss")) {
            url += ".scss";
        }

        if (url.startsWith("file:")) {
            url = url.replace("file:", "");
        }

        Resource resource = resourcePatternResolver.getResource(url);

        if (!resourceExists(resource)) {
            url = "classpath:" + url;
            resource = resourcePatternResolver.getResource(url);
        }

        if (!resourceExists(resource)) {
            final int lastSlash = url.lastIndexOf('/') + 1;
            String cleanUrl = url.substring(0, lastSlash);
            cleanUrl = cleanUrl + "_" + url.substring(lastSlash, url.length());
            resource = resourcePatternResolver.getResource(cleanUrl);
        }

        if (resourceExists(resource) && resource.isReadable()) {
            try {
                if (resource.getURI().getScheme().equals("jar")) {
                    File tempFile = File.createTempFile("wro", ".tmp");
                    tempFile.deleteOnExit();
                    IOUtils.copy(resource.getInputStream(), new FileOutputStream(tempFile));
                    file = Optional.of(tempFile);
                } else {
                    file = Optional.of(resource.getFile());
                }
            } catch (IOException e) {
                //this is fine
            }
        }

        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream locate(final String uri) throws IOException {
        Validate.notNull(uri, "URI cannot be NULL!");
        LOG.debug("loading scss file: {}", uri);
        return new FileInputStream(getScssFile(uri).get());
    }

    /**
     * Check that a resource exists without throwing an exception when it does not exist.
     *
     * Recent version of WRO throw an IllegalArgumentException when the resource does not exist.
     * The exists() check should not throw an exception when the resource does not exist.
     *
     * The stack trace is suppressed unless debug is enabled.
     *
     * @param resource The resource to check.
     * @return TRUE if resource exists, FALSE otherwise.
     */
    private boolean resourceExists(Resource resource) {
        try {
            return resource.exists();
        } catch (IllegalArgumentException e) {
            if (LOG.isDebugEnabled()) {
                e.printStackTrace();
            }
        }

        return false;
    }

}
