package edu.tamu.weaver.auth.controller.handler;

import static edu.tamu.weaver.auth.AuthConstants.UNAUTHORIZED_API_RESPONSE;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.weaver.response.ApiResponse;

@RestController
@ControllerAdvice
public class AuthRestExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse handleAccessDeniedExceptionRest(AccessDeniedException exception) {
        return UNAUTHORIZED_API_RESPONSE;
    }

}