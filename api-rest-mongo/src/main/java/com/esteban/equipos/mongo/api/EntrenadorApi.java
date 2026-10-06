package com.esteban.equipos.mongo.api;
import com.esteban.equipos.mongo.model.Entrenador;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequestMapping("/api/entrenadores")
public interface EntrenadorApi {
 @GetMapping ResponseEntity<List<Entrenador>> listar();
 @GetMapping("/{id}") ResponseEntity<Entrenador> buscar(@PathVariable("id") Long id);
 @PostMapping ResponseEntity<Entrenador> crear(@Valid @RequestBody Entrenador datos);
 @PutMapping("/{id}") ResponseEntity<Entrenador> actualizar(@PathVariable("id") Long id,@Valid @RequestBody Entrenador datos);
 @DeleteMapping("/{id}") ResponseEntity<Void> eliminar(@PathVariable("id") Long id);
}
