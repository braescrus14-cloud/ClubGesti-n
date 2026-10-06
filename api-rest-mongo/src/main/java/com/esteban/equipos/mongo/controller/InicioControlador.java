package com.esteban.equipos.mongo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class InicioControlador {
 @GetMapping("/") public String inicio(){return "forward:/rest.html";}
}
