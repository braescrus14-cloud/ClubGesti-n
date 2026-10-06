package com.esteban.equipos.mongo.model;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
@Document(collection="clubes")
public class Club extends Entidad {
 private String nombre;
 // Relaciones unidireccionales: se guardan IDs y se resuelven los documentos.
 @DocumentReference private Entrenador entrenador;
 @DocumentReference private Asociacion asociacion;
 @DocumentReference private List<Jugador> jugadores = new ArrayList<>();
 @DocumentReference private List<Competicion> competiciones = new ArrayList<>();
 public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;}
 public Entrenador getEntrenador(){return entrenador;} public void setEntrenador(Entrenador v){entrenador=v;}
 public Asociacion getAsociacion(){return asociacion;} public void setAsociacion(Asociacion v){asociacion=v;}
 public List<Jugador> getJugadores(){return jugadores;} public void setJugadores(List<Jugador> v){jugadores=v;}
 public List<Competicion> getCompeticiones(){return competiciones;} public void setCompeticiones(List<Competicion> v){competiciones=v;}
}
