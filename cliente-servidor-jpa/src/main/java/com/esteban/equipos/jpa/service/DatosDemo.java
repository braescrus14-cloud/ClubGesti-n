package com.esteban.equipos.jpa.service;
import com.esteban.equipos.jpa.model.*;
import com.esteban.equipos.jpa.dto.ClubFormulario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
@Service
public class DatosDemo {
 private final EquiposServicio servicio;
 public DatosDemo(EquiposServicio servicio){this.servicio=servicio;}
 @Transactional("jpaTransactionManager") public synchronized boolean cargar(){
  if(!servicio.estaVacio())return false;
  Asociacion a=new Asociacion();a.setNombre("Liga Regional Demo");a.setSiglas("LRD");a.setPais("Colombia");a.setPresidente("Mariana Torres");a=servicio.guardarCatalogo(Asociacion.class,null,a);
  Competicion copa=new Competicion();copa.setNombre("Copa Horizonte Demo");copa.setMontoPremio(5000000);copa.setFechaInicio(LocalDate.of(2026,10,1));copa.setFechaFin(LocalDate.of(2026,12,15));copa=servicio.guardarCatalogo(Competicion.class,null,copa);
  Competicion torneo=new Competicion();torneo.setNombre("Torneo Regional Demo");torneo.setMontoPremio(3000000);torneo.setFechaInicio(LocalDate.of(2026,9,1));torneo.setFechaFin(LocalDate.of(2026,11,30));torneo=servicio.guardarCatalogo(Competicion.class,null,torneo);
  String[][] nombres={{"Ana","Rojas"},{"Carlos","Vega"}};
  String[] posiciones={"Portero","Defensa","Mediocampista","Delantero"};int[] dorsales={1,4,8,9};
  for(int equipo=0;equipo<2;equipo++){
   Entrenador e=new Entrenador();e.setNombre(nombres[equipo][0]);e.setApellido(nombres[equipo][1]);e.setEdad(38+equipo*4);e.setNacionalidad("Colombiana");e=servicio.guardarCatalogo(Entrenador.class,null,e);
   ClubFormulario f=new ClubFormulario();f.setNombre(equipo==0?"Horizonte FC Demo":"Atlético Sierra Demo");f.setEntrenadorId(e.getId());f.setAsociacionId(a.getId());f.setCompeticionIds(equipo==0?List.of(copa.getId(),torneo.getId()):List.of(copa.getId()));Club c=servicio.guardarClub(null,f);
   for(int i=0;i<4;i++){Jugador j=new Jugador();j.setNombre(new String[]{"Samuel","Diego","Mateo","Daniel"}[i]);j.setApellido(equipo==0?"Luna":"Ríos");j.setNumero(dorsales[i]);j.setPosicion(posiciones[i]);j.setClubId(c.getId());servicio.guardarJugador(null,j);}
  }return true;
 }
}
