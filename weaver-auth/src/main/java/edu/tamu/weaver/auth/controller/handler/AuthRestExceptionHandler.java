package edu.tamu.weaver.auth.controller.handler;

import static edu.tamu.weaver.auth.AuthConstants.UNAUTHORIZED_API_RESPONSE;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.weaver.response.ApiResponse;

@RestController
@ControllerAdvice
public class AuthRestExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ApiResponse handleAccessDeniedExceptionRest(AccessDeniedException exception) {
        return UNAUTHORIZED_API_RESPONSE;
    }

}