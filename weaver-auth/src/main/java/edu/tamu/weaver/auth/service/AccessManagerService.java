package edu.tamu.weaver.auth.service;

import edu.tamu.weaver.auth.model.AccessDecision;

public interface AccessManagerService<R> {

    public AccessDecision decideAccess(R object);

}
