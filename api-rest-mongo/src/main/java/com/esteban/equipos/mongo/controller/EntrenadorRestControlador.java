package com.esteban.equipos.mongo.controller;
import com.esteban.equipos.mongo.api.EntrenadorApi;
import com.esteban.equipos.mongo.model.Entrenador;
import com.esteban.equipos.mongo.service.EquiposServicio;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class EntrenadorRestControlador implements EntrenadorApi {
 private final EquiposServicio servicio;
 public EntrenadorRestControlador(EquiposServicio servicio){this.servicio=servicio;}
 @Override public ResponseEntity<List<Entrenador>> listar(){return ResponseEntity.ok(servicio.listar(Entrenador.class));}
 @Override public ResponseEntity<Entrenador> buscar(Long id){return ResponseEntity.ok(servicio.buscar(Entrenador.class,id));}
 @Override public ResponseEntity<Entrenador> crear(Entrenador datos){Long id=null;Entrenador creado=servicio.guardarCatalogo(Entrenador.class,id,datos);return ResponseEntity.created(URI.create("/api/entrenadores/"+creado.getId())).body(creado);}
 @Override public ResponseEntity<Entrenador> actualizar(Long id,Entrenador datos){return ResponseEntity.ok(servicio.guardarCatalogo(Entrenador.class,id,datos));}
 @Override public ResponseEntity<Void> eliminar(Long id){servicio.eliminar(Entrenador.class,id);return ResponseEntity.noContent().build();}
}
