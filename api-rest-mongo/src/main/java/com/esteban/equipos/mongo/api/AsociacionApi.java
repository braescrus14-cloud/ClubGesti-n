package com.esteban.equipos.mongo.api;
import com.esteban.equipos.mongo.model.Asociacion;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequestMapping("/api/asociaciones")
public interface AsociacionApi {
 @GetMapping ResponseEntity<List<Asociacion>> listar();
 @GetMapping("/{id}") ResponseEntity<Asociacion> buscar(@PathVariable("id") Long id);
 @PostMapping ResponseEntity<Asociacion> crear(@Valid @RequestBody Asociacion datos);
 @PutMapping("/{id}") ResponseEntity<Asociacion> actualizar(@PathVariable("id") Long id,@Valid @RequestBody Asociacion datos);
 @DeleteMapping("/{id}") ResponseEntity<Void> eliminar(@PathVariable("id") Long id);
}
