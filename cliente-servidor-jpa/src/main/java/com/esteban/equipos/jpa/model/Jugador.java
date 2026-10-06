package com.esteban.equipos.jpa.model;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
@Entity
@Table(name="jugadores", uniqueConstraints=@UniqueConstraint(name="dorsal_unico_club", columnNames={"id_club","numero"}))
public class Jugador extends Entidad {
 // Solo lectura: la FK la administra Club.jugadores mediante @JoinColumn.
 @Column(name="id_club",insertable=false,updatable=false) private Long clubId;
 public Long getClubId(){return clubId;}
 public void setClubId(Long v){clubId=v;}

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
