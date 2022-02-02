package edu.tamu.weaver.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;
import edu.tamu.weaver.user.model.AbstractWeaverUser;

public abstract class UserCredentialsService<U extends AbstractWeaverUser, R extends AbstractWeaverUserRepo<U>> {

    @Autowired
    protected R userRepo;

    @Value("${app.authority.admins}")
    protected String[] admins;

    public Credentials buildAnonymousCredentials() {
        Credentials anonymousCredentials = new Credentials();
        anonymousCredentials.setAffiliation("NA");
        anonymousCredentials.setLastName("Anonymous");
        anonymousCredentials.setFirstName("Role");
        anonymousCredentials.setNetid("anonymous-" + Math.round(Math.random() * 100000));
        anonymousCredentials.setUin("000000000");
        anonymousCredentials.setExp("1436982214754");
        anonymousCredentials.setEmail("helpdesk@library.tamu.edu");
        anonymousCredentials.setRole(getAnonymousRole());
        return anonymousCredentials;
    }

    public abstract U updateUserByCredentials(Credentials credentials);

    public abstract String getAnonymousRole();

}
