package com.esteban.equipos.mongo.api;
import com.esteban.equipos.mongo.model.Jugador;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequestMapping("/api/jugadores")
public interface JugadorApi {
 @GetMapping ResponseEntity<List<Jugador>> listar();
 @GetMapping("/{id}") ResponseEntity<Jugador> buscar(@PathVariable("id") Long id);
 @PostMapping ResponseEntity<Jugador> crear(@Valid @RequestBody Jugador datos);
 @PutMapping("/{id}") ResponseEntity<Jugador> actualizar(@PathVariable("id") Long id,@Valid @RequestBody Jugador datos);
 @DeleteMapping("/{id}") ResponseEntity<Void> eliminar(@PathVariable("id") Long id);
}
