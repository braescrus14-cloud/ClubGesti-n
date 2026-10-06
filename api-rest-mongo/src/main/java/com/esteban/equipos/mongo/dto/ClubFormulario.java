package com.esteban.equipos.mongo.dto;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
public class ClubFormulario {
 @NotBlank @Size(max=100) private String nombre;
 @NotNull @Positive private Long entrenadorId;
 @NotNull @Positive private Long asociacionId;
 @NotNull private List<@NotNull @Positive Long> jugadorIds=new ArrayList<>();
 @NotNull private List<@NotNull @Positive Long> competicionIds=new ArrayList<>();
 public String getNombre(){return nombre;} public void setNombre(String v){nombre=v==null?null:v.strip();}
 public Long getEntrenadorId(){return entrenadorId;} public void setEntrenadorId(Long v){entrenadorId=v;}
 public Long getAsociacionId(){return asociacionId;} public void setAsociacionId(Long v){asociacionId=v;}
 public List<Long> getJugadorIds(){return jugadorIds;} public void setJugadorIds(List<Long> v){jugadorIds=v;}
 public List<Long> getCompeticionIds(){return competicionIds;} public void setCompeticionIds(List<Long> v){competicionIds=v;}
}
