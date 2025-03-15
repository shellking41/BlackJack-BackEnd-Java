package com.casino.blackjack.Exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final  Map<String,Object> errorResponse=new HashMap<>();

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleEntityNotFoundException(EntityNotFoundException ex){

        errorResponse.put("timeStamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error","Resource not found");
        errorResponse.put("message",ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleUserNotFoundException(UserNotFoundException ex){

        errorResponse.put("timeStamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error","Resource not found");
        errorResponse.put("message",ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(InvalidBetException.class)
    public ResponseEntity<Map<String,Object>> handleInvalidBetException(InvalidBetException ex){
        errorResponse.put("timeStamp",LocalDateTime.now());
        errorResponse.put("status",HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error","Bad Request");
        errorResponse.put("message",ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalStateException(IllegalStateException ex){
        errorResponse.put("timeStamp",LocalDateTime.now());
        errorResponse.put("status",HttpStatus.NOT_ACCEPTABLE.value());
        errorResponse.put("error","Not Acceptable");
        errorResponse.put("message",ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(errorResponse);
    }
@ExceptionHandler(HttpMessageNotReadableException.class)
//ha mas a responsenak a tipusa akkor ezt kapjuk
public ResponseEntity<Map<String,Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex){
    errorResponse.put("timeStamp",LocalDateTime.now());
    errorResponse.put("status",HttpStatus.NOT_FOUND.value());
    errorResponse.put("error","Not Found");
    errorResponse.put("message",ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
}

@ExceptionHandler(ConstraintViolationException.class)
//ez meg akkor van ha annotacioban @sizet,@min,@max adtunk es nem megfelelo akkor ezt dobja
    public ResponseEntity<Map<String,Object>> handleConstraintViolationException(ConstraintViolationException ex){
        errorResponse.put("timeStamp",LocalDateTime.now());
        errorResponse.put("status",HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error","Bad Request");
        errorResponse.put("message",ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    //ha a  @notblank,@notnull-nel rosszat adunk meg akkor ez ketcheli ha pl null a erteke a mezonek, de kell hozza a @valid
    public ResponseEntity<Map<String,Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        errorResponse.put("timeStamp",LocalDateTime.now());
        errorResponse.put("status",HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error","Bad Request");
        // Kinyerjük a validálási hibákat
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)  // Kivesszük az üzenetet (pl. 'email is required')
                .collect(Collectors.joining(", "));  // Ha több hiba van, egyesítjük őket

        errorResponse.put("message", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(GameStateNotFoundException.class)

    public ResponseEntity<Map<String,Object>> handleGameStateNotFoundException(GameStateNotFoundException ex){
        errorResponse.put("timeStamp",LocalDateTime.now());
        errorResponse.put("status",HttpStatus.NOT_FOUND.value());
        errorResponse.put("error","Not Found");
        errorResponse.put("message",ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleCardNotFoundException(CardNotFoundException ex){
        errorResponse.put("timeStamp",LocalDateTime.now());
        errorResponse.put("status",HttpStatus.NOT_FOUND.value());
        errorResponse.put("error","Not Found");
        errorResponse.put("message",ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGlobalException(Exception ex){

        errorResponse.put("timeStamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error","Resource not found");
        errorResponse.put("message",ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
