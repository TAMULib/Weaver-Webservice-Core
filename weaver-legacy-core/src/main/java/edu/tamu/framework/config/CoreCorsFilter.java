/* 
 * CoreCorsFilter.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Cross-Origin Resource Sharing filter.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
@Component
public class CoreCorsFilter implements Filter {

    @Value("${app.security.allow-access}")
    private String[] hosts;

    /**
     * Filter to add appropriate access control.
     *
     * @param req
     *            ServletRequest
     * @param res
     *            ServletResponse
     * @param chain
     *            FilterChain
     * 
     * @exception IOException
     * @exception ServletException
     * 
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        for (String host : hosts) {
            if (host.equals(request.getHeader("Origin"))) {
                response.setHeader("Access-Control-Allow-Origin", host);
            }
        }

        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with, jwt, data, x-forwarded-for");

        chain.doFilter(req, res);
    }

    /**
     * Initialize CORS filter.
     *
     * @param filterConfig
     *            FilterConfig
     *
     */
    public void init(FilterConfig filterConfig) {

    }

    /**
     * Destroy method.
     *
     */
    public void destroy() {

    }

}
