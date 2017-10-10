package edu.tamu.weaver.data.model.repo.impl;

import static edu.tamu.weaver.response.ApiAction.CREATE;
import static edu.tamu.weaver.response.ApiAction.DELETE;
import static edu.tamu.weaver.response.ApiAction.READ;
import static edu.tamu.weaver.response.ApiAction.UPDATE;
import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import edu.tamu.weaver.data.model.WeaverEntity;
import edu.tamu.weaver.data.model.repo.WeaverRepo;
import edu.tamu.weaver.data.model.repo.WeaverRepoCustom;
import edu.tamu.weaver.response.ApiResponse;

public abstract class AbstractWeaverRepoImpl<M extends WeaverEntity, R extends WeaverRepo<M>> implements WeaverRepoCustom<M> {

	@Autowired
	protected R weaverRepo;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	protected abstract String getChannel();

	@Override
	public M create(M model) {
		model = weaverRepo.save(model);
		simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(SUCCESS, CREATE, model));
		return model;
	}

	@Override
	public M read(Long id) {
		M model = weaverRepo.findOne(id);
		simpMessagingTemplate.convertAndSend(getChannel(), new ApiResponse(model != null ? SUCCESS : ERROR, READ, model));
		return model;
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

}
