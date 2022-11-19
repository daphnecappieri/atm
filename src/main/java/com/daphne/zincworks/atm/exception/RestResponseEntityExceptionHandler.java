//package com.daphne.zincworks.atm.exception;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import java.nio.file.AccessDeniedException;
//
//@ControllerAdvice
//public class RestResponseEntityExceptionHandler
//        extends ResponseEntityExceptionHandler {
//
//    @ExceptionHandler({ AccessDeniedException.class })
//    public ResponseEntity<String> handleAccessDeniedException(
//            Exception ex, WebRequest request) {
//        return new ResponseEntity<String>(
//                "Incorrect pin", new HttpHeaders(), HttpStatus.FORBIDDEN);
//    }
//
//}