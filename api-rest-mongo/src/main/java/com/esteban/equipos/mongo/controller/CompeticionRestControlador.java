package com.esteban.equipos.mongo.controller;
import com.esteban.equipos.mongo.api.CompeticionApi;
import com.esteban.equipos.mongo.model.Competicion;
import com.esteban.equipos.mongo.service.EquiposServicio;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class CompeticionRestControlador implements CompeticionApi {
 private final EquiposServicio servicio;
 public CompeticionRestControlador(EquiposServicio servicio){this.servicio=servicio;}
 @Override public ResponseEntity<List<Competicion>> listar(){return ResponseEntity.ok(servicio.listar(Competicion.class));}
 @Override public ResponseEntity<Competicion> buscar(Long id){return ResponseEntity.ok(servicio.buscar(Competicion.class,id));}
 @Override public ResponseEntity<Competicion> crear(Competicion datos){Long id=null;Competicion creado=servicio.guardarCatalogo(Competicion.class,id,datos);return ResponseEntity.created(URI.create("/api/competiciones/"+creado.getId())).body(creado);}
 @Override public ResponseEntity<Competicion> actualizar(Long id,Competicion datos){return ResponseEntity.ok(servicio.guardarCatalogo(Competicion.class,id,datos));}
 @Override public ResponseEntity<Void> eliminar(Long id){servicio.eliminar(Competicion.class,id);return ResponseEntity.noContent().build();}
}
