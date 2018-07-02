package edu.tamu.weaver.auth.controller.handler;

import static edu.tamu.weaver.auth.AuthConstants.UNAUTHORIZED_API_RESPONSE;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.weaver.auth.exception.CredentialsNotFoundException;
import edu.tamu.weaver.auth.exception.UserNotFoundException;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

@RestController
@ControllerAdvice
public class AuthRestExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ApiResponse handleAccessDeniedException(AccessDeniedException exception) {
        return UNAUTHORIZED_API_RESPONSE;
    }

    @ExceptionHandler(CredentialsNotFoundException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse handleCredentialsNotFoundException(CredentialsNotFoundException exception) {
        return new ApiResponse(ApiStatus.ERROR, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse handleUserNotFoundException(UserNotFoundException exception) {
        return new ApiResponse(ApiStatus.ERROR, exception.getMessage());
    }
}