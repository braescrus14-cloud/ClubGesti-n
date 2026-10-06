package com.esteban.equipos.jpa.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages="com.esteban.equipos.jpa.controller")
public class Navegacion {
    @Value("${app.api-url}") private String apiUrl;
    @ModelAttribute("apiUrl") public String apiUrl() { return apiUrl; }
}
