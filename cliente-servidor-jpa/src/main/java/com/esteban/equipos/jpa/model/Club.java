package com.esteban.equipos.jpa.model;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
@Entity
@Table(name="clubes")
public class Club extends Entidad {
 @Column(nullable=false,length=100) private String nombre;
 @OneToOne(fetch=FetchType.LAZY,optional=false)
 @JoinColumn(name="entrenador_id",nullable=false,unique=true) private Entrenador entrenador;
 @ManyToOne(fetch=FetchType.LAZY,optional=false)
 @JoinColumn(name="asociacion_id",nullable=false) private Asociacion asociacion;
 @OneToMany(fetch=FetchType.LAZY)
 @JoinColumn(name="id_club")
 @OnDelete(action=OnDeleteAction.CASCADE)
 @BatchSize(size=50) private List<Jugador> jugadores=new ArrayList<>();
 @ManyToMany(fetch=FetchType.LAZY)
 @JoinTable(name="clubes_competiciones",joinColumns=@JoinColumn(name="club_id"),inverseJoinColumns=@JoinColumn(name="competicion_id"))
 @OnDelete(action=OnDeleteAction.CASCADE)
 @BatchSize(size=50) private List<Competicion> competiciones=new ArrayList<>();
 public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;}
 public Entrenador getEntrenador(){return entrenador;} public void setEntrenador(Entrenador v){entrenador=v;}
 public Asociacion getAsociacion(){return asociacion;} public void setAsociacion(Asociacion v){asociacion=v;}
 public List<Jugador> getJugadores(){return jugadores;} public void setJugadores(List<Jugador> v){jugadores=v;}
 public List<Competicion> getCompeticiones(){return competiciones;} public void setCompeticiones(List<Competicion> v){competiciones=v;}
}
