package com.esteban.equipos.jpa.controller;
import com.esteban.equipos.jpa.model.Competicion;
import com.esteban.equipos.jpa.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/web/competiciones")
public class CompeticionWebControlador {
 private final EquiposServicio servicio;
 public CompeticionWebControlador(EquiposServicio servicio){this.servicio=servicio;}
 @GetMapping public String listar(Model m){m.addAttribute("registros",servicio.listar(Competicion.class));return "competiciones/listar";}
 @GetMapping("/nuevo") public String nuevo(Model m){m.addAttribute("registro",new Competicion());m.addAttribute("id",null);return "competiciones/form";}
 @GetMapping("/{id}/editar") public String editar(@PathVariable Long id,Model m){m.addAttribute("registro",servicio.buscar(Competicion.class,id));m.addAttribute("id",id);return "competiciones/form";}
 @PostMapping("/guardar") public String guardar(@RequestParam(required=false) Long id,@Valid @ModelAttribute("registro") Competicion datos,BindingResult errores,Model m,RedirectAttributes flash){
  m.addAttribute("id",id);if(errores.hasErrors())return "competiciones/form";
  try{servicio.guardarCatalogo(Competicion.class,id,datos);}catch(ReglaNegocio e){errores.reject("negocio",e.getMessage());return "competiciones/form";}
  flash.addFlashAttribute("mensaje","Registro guardado.");return "redirect:/web/competiciones";
 }
 @PostMapping("/{id}/eliminar") public String eliminar(@PathVariable Long id,RedirectAttributes flash){
  try{servicio.eliminar(Competicion.class,id);flash.addFlashAttribute("mensaje","Registro eliminado.");}
  catch(ReglaNegocio e){flash.addFlashAttribute("error",e.getMessage());}return "redirect:/web/competiciones";
 }
}
