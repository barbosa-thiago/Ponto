package com.ilia.ponto.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@ToString
@AllArgsConstructor
public class Usuario {
  @Id
  @EqualsAndHashCode.Include
  @Type(type = "org.hibernate.type.UUIDCharType")
  private UUID id;
  @NotEmpty
  @Column(unique = true)
  private String username;
  @NotEmpty
  private String name;

  @OneToMany(mappedBy = "usuario", cascade = CascadeType.MERGE)
  @Column(unique = true)
  @JsonManagedReference
  @ToString.Exclude
  private List<Ponto> ponto;
}