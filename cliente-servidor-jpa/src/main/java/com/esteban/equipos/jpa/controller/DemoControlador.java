package com.esteban.equipos.jpa.controller;
import com.esteban.equipos.jpa.service.DatosDemo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class DemoControlador {
 private final DatosDemo demo;public DemoControlador(DatosDemo demo){this.demo=demo;}
 @PostMapping("/web/demo") public String cargar(RedirectAttributes atributos){boolean creado=demo.cargar();atributos.addFlashAttribute("mensaje",creado?"Ejemplos creados: 2 clubes, 8 jugadores, 2 entrenadores, 1 asociación y 2 competiciones.":"Se conservaron tus datos. Los ejemplos solo se cargan en una base completamente vacía.");return "redirect:/inicio";}
}
