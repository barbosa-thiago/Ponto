package com.ilia.ponto.requestbody;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioPostBody {
  @NotEmpty
  private String password;
  @NotEmpty
  private String username;
  @NotEmpty
  private String name;


}
