/* 
 * WebSocketRequestMappingHandler.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.weaver.mapping.condition;

/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

// TODO: duplicate Spring's RequestMappingInfo

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

        if (destination == null) {
            return null;
        }

        if (this.patterns.isEmpty()) {
            return this;
        }

        List<String> patternList = new ArrayList<String>();

        patternList.addAll(this.patterns);

        // order of set not maintained

        // class annotation combined with method annotation
        // order can either be 0:class, 1:method or 0:method, 1:class

        String app = request.getServletContext().getContextPath();

        String patternltr = (app != null && app.length() > 0) ? app : "";
        String patternrtl = patternltr;

        int j = patternList.size() - 1;

        for (int i = 0; i < patternList.size(); i++, j--) {
            patternltr += patternList.get(i);
            patternrtl += patternList.get(j);
        }

        if (((AntPathMatcher) this.pathMatcher).match(patternltr, destination)) {
            return this;
        }

        if (((AntPathMatcher) this.pathMatcher).match(patternrtl, destination)) {
            return this;
        }

        return null;
    }

}