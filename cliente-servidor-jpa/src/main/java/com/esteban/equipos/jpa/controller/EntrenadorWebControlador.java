package com.esteban.equipos.jpa.controller;
import com.esteban.equipos.jpa.model.Entrenador;
import com.esteban.equipos.jpa.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/web/entrenadores")
public class EntrenadorWebControlador {
 private final EquiposServicio servicio;
 public EntrenadorWebControlador(EquiposServicio servicio){this.servicio=servicio;}
 @GetMapping public String listar(Model m){m.addAttribute("registros",servicio.listar(Entrenador.class));return "entrenadores/listar";}
 @GetMapping("/nuevo") public String nuevo(Model m){m.addAttribute("registro",new Entrenador());m.addAttribute("id",null);return "entrenadores/form";}
 @GetMapping("/{id}/editar") public String editar(@PathVariable Long id,Model m){m.addAttribute("registro",servicio.buscar(Entrenador.class,id));m.addAttribute("id",id);return "entrenadores/form";}
 @PostMapping("/guardar") public String guardar(@RequestParam(required=false) Long id,@Valid @ModelAttribute("registro") Entrenador datos,BindingResult errores,Model m,RedirectAttributes flash){
  m.addAttribute("id",id);if(errores.hasErrors())return "entrenadores/form";
  try{servicio.guardarCatalogo(Entrenador.class,id,datos);}catch(ReglaNegocio e){errores.reject("negocio",e.getMessage());return "entrenadores/form";}
  flash.addFlashAttribute("mensaje","Registro guardado.");return "redirect:/web/entrenadores";
 }
 @PostMapping("/{id}/eliminar") public String eliminar(@PathVariable Long id,RedirectAttributes flash){
  try{servicio.eliminar(Entrenador.class,id);flash.addFlashAttribute("mensaje","Registro eliminado.");}
  catch(ReglaNegocio e){flash.addFlashAttribute("error",e.getMessage());}return "redirect:/web/entrenadores";
 }
}
