package com.esteban.equipos.mongo.api;
import com.esteban.equipos.mongo.model.Club;
import com.esteban.equipos.mongo.dto.ClubFormulario;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequestMapping("/api/clubes")
public interface ClubApi {
 @GetMapping ResponseEntity<List<Club>> listar();
 @GetMapping("/{id}") ResponseEntity<Club> buscar(@PathVariable("id") Long id);
 @PostMapping ResponseEntity<Club> crear(@Valid @RequestBody ClubFormulario datos);
 @PutMapping("/{id}") ResponseEntity<Club> actualizar(@PathVariable("id") Long id,@Valid @RequestBody ClubFormulario datos);
 @DeleteMapping("/{id}") ResponseEntity<Void> eliminar(@PathVariable("id") Long id);
}
