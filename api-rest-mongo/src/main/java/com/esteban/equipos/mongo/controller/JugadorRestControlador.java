package com.esteban.equipos.mongo.controller;
import com.esteban.equipos.mongo.api.JugadorApi;
import com.esteban.equipos.mongo.model.Jugador;
import com.esteban.equipos.mongo.service.EquiposServicio;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class JugadorRestControlador implements JugadorApi {
 private final EquiposServicio servicio;
 public JugadorRestControlador(EquiposServicio servicio){this.servicio=servicio;}
 @Override public ResponseEntity<List<Jugador>> listar(){return ResponseEntity.ok(servicio.listar(Jugador.class));}
 @Override public ResponseEntity<Jugador> buscar(Long id){return ResponseEntity.ok(servicio.buscar(Jugador.class,id));}
 @Override public ResponseEntity<Jugador> crear(Jugador datos){Long id=null;Jugador creado=servicio.guardarCatalogo(Jugador.class,id,datos);return ResponseEntity.created(URI.create("/api/jugadores/"+creado.getId())).body(creado);}
 @Override public ResponseEntity<Jugador> actualizar(Long id,Jugador datos){return ResponseEntity.ok(servicio.guardarCatalogo(Jugador.class,id,datos));}
 @Override public ResponseEntity<Void> eliminar(Long id){servicio.eliminar(Jugador.class,id);return ResponseEntity.noContent().build();}
}
