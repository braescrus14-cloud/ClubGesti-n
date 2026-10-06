package com.esteban.equipos.jpa.model;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
@Entity
@Table(name="entrenadores")
public class Entrenador extends Entidad {
 @NotBlank @Size(max=80) private String nombre;
 public String getNombre(){return nombre;}
 public void setNombre(String v){nombre=v == null ? null : v.strip();}
 @NotBlank @Size(max=80) private String apellido;
 public String getApellido(){return apellido;}
 public void setApellido(String v){apellido=v == null ? null : v.strip();}
 @NotNull @Min(18) @Max(100) private Integer edad;
 public Integer getEdad(){return edad;}
 public void setEdad(Integer v){edad=v;}
 @NotBlank @Size(max=60) private String nacionalidad;
 public String getNacionalidad(){return nacionalidad;}
 public void setNacionalidad(String v){nacionalidad=v == null ? null : v.strip();}
}
