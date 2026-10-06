package com.esteban.equipos.mongo.model;
import jakarta.validation.constraints.*;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="asociaciones")
public class Asociacion extends Entidad {
 @NotBlank @Size(max=100) private String nombre;
 public String getNombre(){return nombre;}
 public void setNombre(String v){nombre=v == null ? null : v.strip();}
 @NotBlank @Size(max=15) private String siglas;
 public String getSiglas(){return siglas;}
 public void setSiglas(String v){siglas=v == null ? null : v.strip();}
 @NotBlank @Size(max=60) private String pais;
 public String getPais(){return pais;}
 public void setPais(String v){pais=v == null ? null : v.strip();}
 @NotBlank @Size(max=100) private String presidente;
 public String getPresidente(){return presidente;}
 public void setPresidente(String v){presidente=v == null ? null : v.strip();}
}
