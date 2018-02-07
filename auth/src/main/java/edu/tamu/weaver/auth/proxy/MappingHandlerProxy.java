package edu.tamu.weaver.auth.proxy;

import java.util.Map;
import java.util.Optional;

public interface MappingHandlerProxy<I, M, R> {

    public Optional<M> getHandler(R object);

    public Map<I, M> getAllHandlers();

}
