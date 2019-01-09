package wro4j.http.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.http.handler.ReloadCacheRequestHandler;

public class CustomRequestHandler extends ReloadCacheRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CustomRequestHandler.class);

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        LOG.debug("CustomRequestHandler is reloading the cache");
        super.handle(request, response);
    }

    /**
     * ReloadCacheRequestHandler is only enabled in debug mode by default. We need it in production to reload the cache dynamically after theme updates.
     * 
     * TODO: Consider ways of restricting access to the reload end-point to prevent rogue refreshes of the cache.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
