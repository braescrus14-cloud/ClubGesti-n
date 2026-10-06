package com.esteban.equipos.mongo.model;
import jakarta.validation.constraints.*;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="jugadores")
public class Jugador extends Entidad {
 @NotBlank @Size(max=80) private String nombre;
 public String getNombre(){return nombre;}
 public void setNombre(String v){nombre=v == null ? null : v.strip();}
 @NotBlank @Size(max=80) private String apellido;
 public String getApellido(){return apellido;}
 public void setApellido(String v){apellido=v == null ? null : v.strip();}
 @NotNull @Min(1) @Max(99) private Integer numero;
 public Integer getNumero(){return numero;}
 public void setNumero(Integer v){numero=v;}
 @NotBlank @Size(max=50) private String posicion;
 public String getPosicion(){return posicion;}
 public void setPosicion(String v){posicion=v == null ? null : v.strip();}
}
