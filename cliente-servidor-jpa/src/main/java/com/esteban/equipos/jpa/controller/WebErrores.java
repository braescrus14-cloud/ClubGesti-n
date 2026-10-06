package com.esteban.equipos.jpa.controller;
import com.esteban.equipos.jpa.service.ReglaNegocio;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionException;
import jakarta.servlet.http.HttpServletResponse;
@ControllerAdvice(annotations=Controller.class,basePackages="com.esteban.equipos.jpa.controller")
public class WebErrores {
 @ExceptionHandler(ReglaNegocio.class) public String negocio(ReglaNegocio e,Model m,HttpServletResponse r){r.setStatus(e.getEstado());m.addAttribute("detalle",e.getMessage());return "soporte/error";}
 @ExceptionHandler({DataAccessException.class,TransactionException.class}) public String datos(Exception e,Model m,HttpServletResponse r){r.setStatus(503);m.addAttribute("detalle","No fue posible completar la operación. Revisa MongoDB o actualiza la página e inténtalo de nuevo.");return "soporte/error";}
}
