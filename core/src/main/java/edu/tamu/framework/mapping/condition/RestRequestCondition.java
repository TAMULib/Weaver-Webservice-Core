/* 
 * WebSocketRequestMappingHandler.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.mapping.condition;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

//TODO: duplicate Spring's RequestMappingInfo

/**
 * Rest request condition.
 * 
 * @author <a href="mailto:jmicah@library.tamu.edu">Micah Cooper</a>
 * @author <a href="mailto:jcreel@library.tamu.edu">James Creel</a>
 * @author <a href="mailto:huff@library.tamu.edu">Jeremy Huff</a>
 * @author <a href="mailto:jsavell@library.tamu.edu">Jason Savell</a>
 * @author <a href="mailto:wwelling@library.tamu.edu">William Welling</a>
 *
 */
public class RestRequestCondition implements RequestCondition<RestRequestCondition> {

	private final Set<String> patterns;

	private final PathMatcher pathMatcher;

	public RestRequestCondition(String... paths) {
		this(Arrays.asList(paths), null);
	}

	public RestRequestCondition(String[] paths, PathMatcher pathMatcher) {
		this(Arrays.asList(paths), pathMatcher);
	}

	public RestRequestCondition(Collection<String> paths, PathMatcher pathMatcher) {
		this.patterns = Collections.unmodifiableSet(new HashSet<String>(paths));
		this.pathMatcher = (pathMatcher != null ? pathMatcher : (PathMatcher) new AntPathMatcher());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRequestCondition combine(RestRequestCondition other) {
		Set<String> allRoles = new LinkedHashSet<String>(this.patterns);
		allRoles.addAll(other.patterns);
		return new RestRequestCondition(allRoles, this.pathMatcher);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(RestRequestCondition other, HttpServletRequest request) {
		return CollectionUtils.removeAll(other.patterns, this.patterns).size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RestRequestCondition getMatchingCondition(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String destination = uri.contains("?") ? uri.split("?")[0] : uri;

		boolean match = true;
		for (String pattern : this.patterns) {
			if (!destination.toLowerCase().contains(pattern.toLowerCase())) {
				match = false;
			}
		}

		if (match) {
			return this;
		}

		// to match if type with class annotation include a path variable
		String fullPathPattern = "";

		List<String> patternList = new ArrayList<String>();
        
        patternList.addAll(patterns);
        
        for (int i = patternList.size()-1; i >= 0; i--) {
            fullPathPattern = patternList.get(i) + fullPathPattern;
        }
        
        if (((AntPathMatcher) this.pathMatcher).match(fullPathPattern, destination)) {
            return this;
        }

		return null;
	}

}