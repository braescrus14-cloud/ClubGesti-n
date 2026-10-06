package com.esteban.equipos.mongo.model;
import org.springframework.data.annotation.Id;
public abstract class Entidad {
 @Id private Long id;
 public Long getId(){return id;}
 public void setId(Long id){this.id=id;}
}
