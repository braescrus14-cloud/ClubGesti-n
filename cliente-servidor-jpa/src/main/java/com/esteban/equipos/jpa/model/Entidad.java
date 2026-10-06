package com.esteban.equipos.jpa.model;
import jakarta.persistence.*;
@MappedSuperclass
public abstract class Entidad {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 public Long getId(){return id;} public void setId(Long id){this.id=id;}
}
