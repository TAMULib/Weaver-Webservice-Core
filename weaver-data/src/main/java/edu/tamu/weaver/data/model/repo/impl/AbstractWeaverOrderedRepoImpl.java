package edu.tamu.weaver.data.model.repo.impl;

import static edu.tamu.weaver.response.ApiAction.REMOVE;
import static edu.tamu.weaver.response.ApiAction.REORDER;
import static edu.tamu.weaver.response.ApiAction.SORT;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.weaver.data.model.OrderedBaseEntity;
import edu.tamu.weaver.data.model.WeaverEntity;
import edu.tamu.weaver.data.model.repo.WeaverOrderedRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;
import edu.tamu.weaver.data.service.OrderedEntityService;
import edu.tamu.weaver.response.ApiResponse;

public abstract class AbstractWeaverOrderedRepoImpl<M extends WeaverEntity, R extends WeaverRepo<M>> extends AbstractWeaverRepoImpl<M, R> implements WeaverOrderedRepoCustom<M> {

    @Autowired
    private OrderedEntityService orderedEntityService;

    @Override
    public void reorder(Long src, Long dest) {
        orderedEntityService.reorder(getModelClass(), src, dest);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, REORDER, weaverRepo.findAllByOrderByPositionAsc()));
    }

    @Override
    public void sort(String column) {
        orderedEntityService.sort(getModelClass(), column);
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, SORT, weaverRepo.findAllByOrderByPositionAsc()));
    }

    @Override
    public void remove(M model) {
        orderedEntityService.remove(weaverRepo, getModelClass(), ((OrderedBaseEntity) model).getPosition());
        simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, REMOVE, weaverRepo.findAllByOrderByPositionAsc()));
    }

}
