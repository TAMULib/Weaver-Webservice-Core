package edu.tamu.weaver.data.model.repo.impl;

import static edu.tamu.weaver.response.ApiAction.BROADCAST;
import static edu.tamu.weaver.response.ApiAction.CREATE;
import static edu.tamu.weaver.response.ApiAction.DELETE;
import static edu.tamu.weaver.response.ApiAction.READ;
import static edu.tamu.weaver.response.ApiAction.UPDATE;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import edu.tamu.weaver.data.model.WeaverEntity;
import edu.tamu.weaver.data.model.repo.WeaverRepo;
import edu.tamu.weaver.data.model.repo.WeaverRepoCustom;
import edu.tamu.weaver.response.ApiResponse;

public abstract class AbstractWeaverRepoImpl<M extends WeaverEntity, R extends WeaverRepo<M>> implements WeaverRepoCustom<M> {

    @Autowired
    protected R weaverRepo;

    @Lazy
    @Autowired
    protected SimpMessagingTemplate simpMessagingTemplate;

    protected abstract String getChannel();

    @Override
    public M create(M model) {
        model = weaverRepo.save(model);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, CREATE, model));
        return model;
    }

    @Override
    public M read(Long id) {
        return weaverRepo.findOne(id);
    }

    @Override
    public M update(M model) {
        model = weaverRepo.save(model);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, UPDATE, model));
        return model;
    }

    @Override
    public void delete(M model) {
        Long id = model.getId();
        weaverRepo.delete(id);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, DELETE, model));
    }
    
    @Override
    public void broadcast(List<M> repo) {
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, BROADCAST, repo));
    }
    
    @Override
    public void broadcast(M model) {
    	simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, READ, model));
    }
    
    @Override
    public void broadcast(Long id) {
    	broadcast(read(id));
    }

}
