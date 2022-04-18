package edu.tamu.weaver.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import edu.tamu.weaver.response.ApiResponse.Meta;

@JsonComponent
public class ApiResponseSerializer extends JsonSerializer<ApiResponse> {

    @Override
    public void serialize(ApiResponse apiResponse, JsonGenerator jgen, SerializerProvider serializers)
        throws IOException {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        if (request.getHeader("x-wvr-unwrap") != null) {

            Meta meta = apiResponse.getMeta();
            String message = meta.getMessage();
            switch (meta.getStatus()) {
            case ERROR:
                throw new ApiResponseStatusException(message, 500);
            case INVALID:
                throw new ApiResponseStatusException(message, 400);
            case REFRESH:
            case UNAUTHORIZED:
                throw new ApiResponseStatusException(message, 401);
            case INFO:
            case SUCCESS:
            case WARNING:
            default:
                break;
            }

            List<Object> values = new ArrayList<>(apiResponse.getPayload().values());
            if (values.size() > 1) {
                jgen.writeObject(apiResponse.getPayload());
            } else if (values.size() > 0) {
                jgen.writeObject(values.get(0));
            }

        } else {
            jgen.writeStartObject();
            jgen.writeObjectField("meta", apiResponse.getMeta());
            jgen.writeObjectField("payload", apiResponse.getPayload());
            jgen.writeEndObject();
        }
    }

}
