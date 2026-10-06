package com.esteban.equipos.mongo.service;
public class ReglaNegocio extends RuntimeException {
 private final int estado;
 public ReglaNegocio(int estado,String mensaje){super(mensaje);this.estado=estado;}
 public int getEstado(){return estado;}
}
