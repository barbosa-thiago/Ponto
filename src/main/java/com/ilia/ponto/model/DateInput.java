package com.ilia.ponto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DateInput {
  @NotEmpty
  @Schema(example = "2022-12-31T00:00:00")
  private String dateTime;
}
