package com.esteban.equipos.jpa.controller;
import com.esteban.equipos.jpa.model.*;
import com.esteban.equipos.jpa.dto.ClubFormulario;
import com.esteban.equipos.jpa.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class ClubWebControlador {
 private final EquiposServicio servicio;
 public ClubWebControlador(EquiposServicio servicio){this.servicio=servicio;}
 @GetMapping("/inicio") public String inicio(Model m){m.addAttribute("resumen",servicio.resumen());return "index";}
 @GetMapping("/web/clubes") public String listar(Model m){m.addAttribute("registros",servicio.listar(Club.class));return "clubes/listar";}
 private void opciones(Model m){m.addAttribute("entrenadores",servicio.listar(Entrenador.class));m.addAttribute("asociaciones",servicio.listar(Asociacion.class));m.addAttribute("jugadores",servicio.listar(Jugador.class));m.addAttribute("competiciones",servicio.listar(Competicion.class));}
 @GetMapping("/web/clubes/nuevo") public String nuevo(Model m){m.addAttribute("registro",new ClubFormulario());m.addAttribute("id",null);opciones(m);return "clubes/form";}
 @GetMapping("/web/clubes/{id}/editar") public String editar(@PathVariable Long id,Model m){m.addAttribute("registro",servicio.formulario(id));m.addAttribute("id",id);opciones(m);return "clubes/form";}
 @GetMapping("/web/clubes/{id}") public String detalle(@PathVariable Long id,Model m){m.addAttribute("club",servicio.buscar(Club.class,id));m.addAttribute("registros",servicio.listar(Club.class));return "clubes/listar";}
 @PostMapping("/web/clubes/guardar") public String guardar(@RequestParam(required=false) Long id,@Valid @ModelAttribute("registro") ClubFormulario datos,BindingResult errores,Model m,RedirectAttributes flash){
  m.addAttribute("id",id);
  if(!errores.hasErrors())try{servicio.guardarClub(id,datos);flash.addFlashAttribute("mensaje","Club guardado con sus relaciones.");return "redirect:/web/clubes";}catch(ReglaNegocio e){errores.reject("negocio",e.getMessage());}
  opciones(m);return "clubes/form";
 }
 @PostMapping("/web/clubes/{id}/eliminar") public String eliminar(@PathVariable Long id,RedirectAttributes flash){
  try{servicio.eliminar(Club.class,id);flash.addFlashAttribute("mensaje","Club eliminado junto con sus jugadores. Los catálogos compartidos se conservan.");}
  catch(ReglaNegocio e){flash.addFlashAttribute("error",e.getMessage());}return "redirect:/web/clubes";
 }
}
