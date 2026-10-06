package com.esteban.equipos.jpa.controller;
import com.esteban.equipos.jpa.model.Jugador;
import com.esteban.equipos.jpa.model.Club;
import com.esteban.equipos.jpa.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/web/jugadores")
public class JugadorWebControlador {
 private final EquiposServicio servicio;
 public JugadorWebControlador(EquiposServicio servicio){this.servicio=servicio;}
 @GetMapping public String listar(Model m){m.addAttribute("clubesNombres",servicio.nombresClubes());m.addAttribute("registros",servicio.listar(Jugador.class));return "jugadores/listar";}
 @GetMapping("/nuevo") public String nuevo(Model m){m.addAttribute("registro",new Jugador());m.addAttribute("id",null);m.addAttribute("clubes",servicio.listar(Club.class));return "jugadores/form";}
 @GetMapping("/{id}/editar") public String editar(@PathVariable Long id,Model m){m.addAttribute("registro",servicio.buscar(Jugador.class,id));m.addAttribute("id",id);m.addAttribute("clubes",servicio.listar(Club.class));return "jugadores/form";}
 @PostMapping("/guardar") public String guardar(@RequestParam(required=false) Long id,@Valid @ModelAttribute("registro") Jugador datos,BindingResult errores,Model m,RedirectAttributes flash){
  m.addAttribute("id",id);if(errores.hasErrors()){m.addAttribute("clubes",servicio.listar(Club.class));return "jugadores/form";}
  try{servicio.guardarJugador(id,datos);}catch(ReglaNegocio e){errores.reject("negocio",e.getMessage());m.addAttribute("clubes",servicio.listar(Club.class));return "jugadores/form";}
  flash.addFlashAttribute("mensaje","Registro guardado.");return "redirect:/web/jugadores";
 }
 @PostMapping("/{id}/eliminar") public String eliminar(@PathVariable Long id,RedirectAttributes flash){
  try{servicio.eliminar(Jugador.class,id);flash.addFlashAttribute("mensaje","Registro eliminado.");}
  catch(ReglaNegocio e){flash.addFlashAttribute("error",e.getMessage());}return "redirect:/web/jugadores";
 }
}
