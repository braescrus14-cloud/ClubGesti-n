package com.esteban.equipos.jpa.controller;
import com.esteban.equipos.jpa.model.Asociacion;
import com.esteban.equipos.jpa.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/web/asociaciones")
public class AsociacionWebControlador {
 private final EquiposServicio servicio;
 public AsociacionWebControlador(EquiposServicio servicio){this.servicio=servicio;}
 @GetMapping public String listar(Model m){m.addAttribute("registros",servicio.listar(Asociacion.class));return "asociaciones/listar";}
 @GetMapping("/nuevo") public String nuevo(Model m){m.addAttribute("registro",new Asociacion());m.addAttribute("id",null);return "asociaciones/form";}
 @GetMapping("/{id}/editar") public String editar(@PathVariable Long id,Model m){m.addAttribute("registro",servicio.buscar(Asociacion.class,id));m.addAttribute("id",id);return "asociaciones/form";}
 @PostMapping("/guardar") public String guardar(@RequestParam(required=false) Long id,@Valid @ModelAttribute("registro") Asociacion datos,BindingResult errores,Model m,RedirectAttributes flash){
  m.addAttribute("id",id);if(errores.hasErrors())return "asociaciones/form";
  try{servicio.guardarCatalogo(Asociacion.class,id,datos);}catch(ReglaNegocio e){errores.reject("negocio",e.getMessage());return "asociaciones/form";}
  flash.addFlashAttribute("mensaje","Registro guardado.");return "redirect:/web/asociaciones";
 }
 @PostMapping("/{id}/eliminar") public String eliminar(@PathVariable Long id,RedirectAttributes flash){
  try{servicio.eliminar(Asociacion.class,id);flash.addFlashAttribute("mensaje","Registro eliminado.");}
  catch(ReglaNegocio e){flash.addFlashAttribute("error",e.getMessage());}return "redirect:/web/asociaciones";
 }
}
