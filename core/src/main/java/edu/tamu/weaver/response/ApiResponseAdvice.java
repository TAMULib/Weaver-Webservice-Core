package edu.tamu.weaver.response;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ApiResponseAdvice {

  @ExceptionHandler(ApiResponseStatusException.class)
  public @ResponseBody ResponseEntity<String> handleApiResponseStatusException(ApiResponseStatusException e) {
    return ResponseEntity.status(e.getStatus()).body(e.getMessage());
  }

}
