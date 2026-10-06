package com.esteban.equipos.mongo.controller;
import com.esteban.equipos.mongo.api.ClubApi;
import com.esteban.equipos.mongo.model.Club;
import com.esteban.equipos.mongo.dto.ClubFormulario;
import com.esteban.equipos.mongo.service.EquiposServicio;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class ClubRestControlador implements ClubApi {
 private final EquiposServicio servicio;
 public ClubRestControlador(EquiposServicio servicio){this.servicio=servicio;}
 @Override public ResponseEntity<List<Club>> listar(){return ResponseEntity.ok(servicio.listar(Club.class));}
 @Override public ResponseEntity<Club> buscar(Long id){return ResponseEntity.ok(servicio.buscar(Club.class,id));}
 @Override public ResponseEntity<Club> crear(ClubFormulario datos){Long id=null;Club creado=servicio.guardarClub(id,datos);return ResponseEntity.created(URI.create("/api/clubes/"+creado.getId())).body(creado);}
 @Override public ResponseEntity<Club> actualizar(Long id,ClubFormulario datos){return ResponseEntity.ok(servicio.guardarClub(id,datos));}
 @Override public ResponseEntity<Void> eliminar(Long id){servicio.eliminar(Club.class,id);return ResponseEntity.noContent().build();}
}
