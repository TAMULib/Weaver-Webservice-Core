/* 
 * CoreExceptionHandler.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.exception;

import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class CoreExceptionHandler {

    @ResponseBody
    @ExceptionHandler(JWTException.class)
    public ResponseEntity<Map> handleJWTException(JWTException ex) {
        Map<String, String> errorMap = new HashMap<String, String>();
        errorMap.put("code", ex.getErrCode());
        errorMap.put("message", ex.getErrMsg());
        return new ResponseEntity<Map>(errorMap, null, HttpStatus.FORBIDDEN);
    }

}