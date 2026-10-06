package com.esteban.equipos.mongo.controller;
import com.esteban.equipos.mongo.api.AsociacionApi;
import com.esteban.equipos.mongo.model.Asociacion;
import com.esteban.equipos.mongo.service.EquiposServicio;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class AsociacionRestControlador implements AsociacionApi {
 private final EquiposServicio servicio;
 public AsociacionRestControlador(EquiposServicio servicio){this.servicio=servicio;}
 @Override public ResponseEntity<List<Asociacion>> listar(){return ResponseEntity.ok(servicio.listar(Asociacion.class));}
 @Override public ResponseEntity<Asociacion> buscar(Long id){return ResponseEntity.ok(servicio.buscar(Asociacion.class,id));}
 @Override public ResponseEntity<Asociacion> crear(Asociacion datos){Long id=null;Asociacion creado=servicio.guardarCatalogo(Asociacion.class,id,datos);return ResponseEntity.created(URI.create("/api/asociaciones/"+creado.getId())).body(creado);}
 @Override public ResponseEntity<Asociacion> actualizar(Long id,Asociacion datos){return ResponseEntity.ok(servicio.guardarCatalogo(Asociacion.class,id,datos));}
 @Override public ResponseEntity<Void> eliminar(Long id){servicio.eliminar(Asociacion.class,id);return ResponseEntity.noContent().build();}
}
