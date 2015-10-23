package edu.tamu.framework.mapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

public class RestRequestCondition implements RequestCondition<RestRequestCondition> {

	private final Set<String> paths;

    public RestRequestCondition(String... paths) {
        this(Arrays.asList(paths));
    }

    public RestRequestCondition(Collection<String> path) {
        this.paths = Collections.unmodifiableSet(new HashSet<String>(path));
    }

	@Override
	public RestRequestCondition combine(RestRequestCondition other) {
		Set<String> allRoles = new LinkedHashSet<String>(this.paths);
		allRoles.addAll(other.paths);
		return new RestRequestCondition(allRoles);
	}

	@Override
	public int compareTo(RestRequestCondition other, HttpServletRequest request) {
		 return CollectionUtils.removeAll(other.paths, this.paths).size();
	}

	@Override
	public RestRequestCondition getMatchingCondition(HttpServletRequest request) {
		
		System.out.println("\nREST GET MATCHING CONDITION\n");
		
		System.out.println("DESTINATION: " +request.getServletPath() + "\n");
       
        String path = request.getServletPath();
        
        System.out.println("\n" + path + "\n");
        
        boolean match = true;
        for (String s : this.paths) {
            if(!path.toLowerCase().contains(s.toLowerCase())) {
            	match = false;
            }
        }
        
        if(match) {
        	System.out.println("\nMATCH\n");
        	return this;
        }
        
        return null;
	}

}