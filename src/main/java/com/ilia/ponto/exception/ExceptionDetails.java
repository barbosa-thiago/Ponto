package com.ilia.ponto.exception;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@SuperBuilder
public class ExceptionDetails {
  private String exception;
  private int status;
  private String developerMessage;
  private String details;
  private LocalDateTime timeStamp;

}