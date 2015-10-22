package edu.tamu.framework.mapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.condition.RequestCondition;

public class RestRequestCondition implements RequestCondition<RestRequestCondition> {

	private final Set<String> path;

    public RestRequestCondition(String... path) {
        this(Arrays.asList(path));
    }

    public RestRequestCondition(Collection<String> path) {
        this.path = Collections.unmodifiableSet(new HashSet<String>(path));
    }

    @Override
    public RestRequestCondition combine(RestRequestCondition other) {
        Set<String> allRoles = new LinkedHashSet<String>(this.path);
        allRoles.addAll(other.path);
        return new RestRequestCondition(allRoles);
    }

    @Override
    public RestRequestCondition getMatchingCondition(HttpServletRequest request) {
    	
    	System.out.println("\nREST GET MATCHING CONDITION\n");
    	 
        try {
            String path = request.getServletPath();
            
            System.out.println("\n" + path + "\n");
            
            boolean match = true;
            for (String s : this.path) {
                if(!path.toLowerCase().contains(s.toLowerCase())) {
                	match = false;
                }
            }
            
            if(match) {
            	System.out.println("\nMATCH\n");
            	return this;
            }
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    @Override
    public int compareTo(RestRequestCondition other, HttpServletRequest request) {
        return org.apache.commons.collections.CollectionUtils.removeAll(other.path, this.path).size();
    }

}