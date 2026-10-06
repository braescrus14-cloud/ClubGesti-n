package com.esteban.equipos.mongo.api;
import com.esteban.equipos.mongo.model.Competicion;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequestMapping("/api/competiciones")
public interface CompeticionApi {
 @GetMapping ResponseEntity<List<Competicion>> listar();
 @GetMapping("/{id}") ResponseEntity<Competicion> buscar(@PathVariable("id") Long id);
 @PostMapping ResponseEntity<Competicion> crear(@Valid @RequestBody Competicion datos);
 @PutMapping("/{id}") ResponseEntity<Competicion> actualizar(@PathVariable("id") Long id,@Valid @RequestBody Competicion datos);
 @DeleteMapping("/{id}") ResponseEntity<Void> eliminar(@PathVariable("id") Long id);
}
