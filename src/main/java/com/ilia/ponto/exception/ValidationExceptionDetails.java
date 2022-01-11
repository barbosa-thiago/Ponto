package com.ilia.ponto.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ValidationExceptionDetails extends ExceptionDetails{
  private String fieldValidationErrors;
  private String fieldsMessage;
}
