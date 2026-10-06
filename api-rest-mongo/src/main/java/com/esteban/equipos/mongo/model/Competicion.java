package com.esteban.equipos.mongo.model;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Document(collection="competiciones")
public class Competicion extends Entidad {
 @NotBlank @Size(max=100) private String nombre;
 public String getNombre(){return nombre;}
 public void setNombre(String v){nombre=v == null ? null : v.strip();}
 @NotNull @Min(0) private Integer montoPremio;
 public Integer getMontoPremio(){return montoPremio;}
 public void setMontoPremio(Integer v){montoPremio=v;}
 @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaInicio;
 public LocalDate getFechaInicio(){return fechaInicio;}
 public void setFechaInicio(LocalDate v){fechaInicio=v;}
 @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaFin;
 public LocalDate getFechaFin(){return fechaFin;}
 public void setFechaFin(LocalDate v){fechaFin=v;}
 @AssertTrue(message="La fecha final debe ser igual o posterior a la inicial") @JsonIgnore
 public boolean isFechasValidas(){return fechaInicio==null || fechaFin==null || !fechaFin.isBefore(fechaInicio);}
}
