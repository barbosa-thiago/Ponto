package com.ilia.ponto.handler;

import com.ilia.ponto.exception.ExceptionDetails;
import com.ilia.ponto.exception.ValidationExceptionDetails;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ExceptionDetails>
      handleConstraintViolationException(ConstraintViolationException exception) {

    return new ResponseEntity<>(
        ExceptionDetails.builder()
            .exception(exception.getClass().getSimpleName())
            .details(exception.getCause().toString())
            .developerMessage(exception.getMessage())
            .status(HttpStatus.BAD_REQUEST.value())
            .timeStamp(LocalDateTime.now())
            .build(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ExceptionDetails> handleBadRequestException(ResponseStatusException exception) {

    return new ResponseEntity<>(
        ExceptionDetails.builder()
            .exception(exception.getClass().getSimpleName())
            .details(exception.getMessage())
            .developerMessage(exception.getMessage())
            .status(HttpStatus.BAD_REQUEST.value())
            .timeStamp(LocalDateTime.now())
            .build(), HttpStatus.BAD_REQUEST);
  }


  @Override
  public ResponseEntity<Object>
  handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                               HttpHeaders headers,
                               HttpStatus status,
                               WebRequest webRequest) {

    List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

    String fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
    String fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage)
        .collect(Collectors.joining(", "));

    ValidationExceptionDetails exceptionDetails = ValidationExceptionDetails.builder()
        .exception(exception.getClass().getSimpleName())
        .details("Field Validation Error")
        .developerMessage(exception.getMessage())
        .status(HttpStatus.BAD_REQUEST.value())
        .timeStamp(LocalDateTime.now())
        .fieldValidationErrors(fields)
        .fieldsMessage(fieldsMessage)
        .build();

    return new ResponseEntity<>(exceptionDetails, headers, status);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception exception,
                                                           @Nullable Object body,
                                                           HttpHeaders headers,
                                                           HttpStatus status,
                                                           WebRequest request) {

    ExceptionDetails exceptionDetails = ExceptionDetails.builder()
        .exception(exception.getClass().getSimpleName())
        .timeStamp(LocalDateTime.now())
        .status(status.value())
        .details(exception.getCause().getMessage())
        .developerMessage(exception.getMessage())
        .build();
    return new ResponseEntity<>(exceptionDetails, headers, status);
  }
}
