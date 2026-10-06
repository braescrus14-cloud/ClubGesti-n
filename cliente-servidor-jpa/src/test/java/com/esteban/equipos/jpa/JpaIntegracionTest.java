package com.esteban.equipos.jpa;
import com.esteban.equipos.jpa.model.*;
import com.esteban.equipos.jpa.dto.ClubFormulario;
import com.esteban.equipos.jpa.service.*;
import com.esteban.equipos.jpa.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:equipos_test;DB_CLOSE_DELAY=-1", "spring.jpa.hibernate.ddl-auto=create-drop"})
class JpaIntegracionTest {
 @Autowired EquiposServicio servicio;
 @Autowired jakarta.persistence.EntityManagerFactory factory;
 @Autowired DatosDemo demo;
 @Autowired ClubRepository clubes;
 @Autowired JugadorRepository jugadores;
 @Autowired EntrenadorRepository entrenadores;
 @Autowired AsociacionRepository asociaciones;
 @Autowired CompeticionRepository competiciones;
 @Autowired JdbcTemplate sql;
 @Autowired WebApplicationContext context;
 MockMvc mvc;
 @BeforeEach void preparar(){
  for(Club c:servicio.listar(Club.class))servicio.eliminar(Club.class,c.getId());
  jugadores.deleteAll();entrenadores.deleteAll();asociaciones.deleteAll();competiciones.deleteAll();
  mvc=MockMvcBuilders.webAppContextSetup(context).build();
 }
 Entrenador entrenador(){Entrenador x=new Entrenador();x.setNombre("Ana");x.setApellido("Pérez");x.setEdad(40);x.setNacionalidad("Colombiana");return servicio.guardarCatalogo(Entrenador.class,null,x);}
 Asociacion asociacion(){Asociacion x=new Asociacion();x.setNombre("Federación de prueba");x.setSiglas("FDP");x.setPais("Colombia");x.setPresidente("María Torres");return servicio.guardarCatalogo(Asociacion.class,null,x);}
 Competicion competicion(){Competicion x=new Competicion();x.setNombre("Copa de prueba");x.setMontoPremio(1000);x.setFechaInicio(LocalDate.of(2026,10,1));x.setFechaFin(LocalDate.of(2026,11,1));return servicio.guardarCatalogo(Competicion.class,null,x);}
 Jugador datosJugador(int n,Long clubId){Jugador j=new Jugador();j.setNombre("Luis");j.setApellido("Díaz");j.setNumero(n);j.setPosicion("Delantero");j.setClubId(clubId);return j;}
 ClubFormulario formulario(Entrenador e,Asociacion a,List<Jugador> js,List<Competicion> cs){ClubFormulario f=new ClubFormulario();f.setNombre("Club de prueba");f.setEntrenadorId(e.getId());f.setAsociacionId(a.getId());f.setJugadorIds(js.stream().map(Jugador::getId).toList());f.setCompeticionIds(cs.stream().map(Competicion::getId).toList());return f;}
 Club club(){return servicio.guardarClub(null,formulario(entrenador(),asociacion(),List.of(),List.of(competicion())));}

 @Test void esquemaTieneForeignKeySinTablaJugadoresIntermedia(){
  var c=club();var j=servicio.guardarJugador(null,datosJugador(10,c.getId()));
  assertEquals(c.getId(),sql.queryForObject("select id_club from jugadores where id=?",Long.class,j.getId()));
  assertEquals(0,sql.queryForObject("select count(*) from information_schema.tables where table_name='CLUBES_JUGADORES'",Integer.class));
  assertEquals(1,sql.queryForObject("select count(*) from clubes_competiciones where club_id=?",Integer.class,c.getId()));
 }
 @Test void cuatroRelacionesSeConsultanFueraDeLaTransaccion(){
  var j=servicio.guardarJugador(null,datosJugador(10,null));var cp=competicion();var c=servicio.guardarClub(null,formulario(entrenador(),asociacion(),List.of(j),List.of(cp)));
  var loaded=servicio.buscar(Club.class,c.getId());assertEquals("Ana",loaded.getEntrenador().getNombre());assertEquals("FDP",loaded.getAsociacion().getSiglas());assertEquals(1,loaded.getJugadores().size());assertEquals(cp.getId(),loaded.getCompeticiones().getFirst().getId());
 }
 @Test void entrenadorNoPuedeCompartirse(){
  var c=club();var f=formulario(c.getEntrenador(),c.getAsociacion(),List.of(),c.getCompeticiones());assertEquals(409,assertThrows(ReglaNegocio.class,()->servicio.guardarClub(null,f)).getEstado());assertEquals(1,clubes.count());
 }
 @Test void jugadorSeTrasladaDesdeSuFormulario(){
  var c1=club();var c2=club();var j=servicio.guardarJugador(null,datosJugador(10,c1.getId()));
  assertEquals(c1.getId(),j.getClubId());j.setClubId(c2.getId());var actualizado=servicio.guardarJugador(j.getId(),j);
  assertEquals(c2.getId(),actualizado.getClubId());assertTrue(servicio.buscar(Club.class,c1.getId()).getJugadores().isEmpty());assertEquals(1,servicio.buscar(Club.class,c2.getId()).getJugadores().size());
 }
 @Test void asignacionDuplicadaEnFormularioClubSeRechaza(){
  var c1=club();var c2=club();var j=servicio.guardarJugador(null,datosJugador(10,c1.getId()));var f=servicio.formulario(c2.getId());f.setJugadorIds(List.of(j.getId()));assertThrows(ReglaNegocio.class,()->servicio.guardarClub(c2.getId(),f));
 }
 @Test void dorsalDuplicadoSeRechazaYNoGuardaElJugador(){
  var c=club();servicio.guardarJugador(null,datosJugador(10,c.getId()));assertThrows(ReglaNegocio.class,()->servicio.guardarJugador(null,datosJugador(10,c.getId())));assertEquals(1,jugadores.count());
 }
 @Test void asociacionYCompeticionSeComparten(){
  var a=asociacion();var cp=competicion();servicio.guardarClub(null,formulario(entrenador(),a,List.of(),List.of(cp)));servicio.guardarClub(null,formulario(entrenador(),a,List.of(),List.of(cp)));assertEquals(2,clubes.count());assertEquals(2,sql.queryForObject("select count(*) from clubes_competiciones",Integer.class));
 }
 @Test void borradoClubHaceCascadaSoloEnJugadores(){
  var c=club();servicio.guardarJugador(null,datosJugador(10,c.getId()));servicio.eliminar(Club.class,c.getId());assertEquals(0,jugadores.count());assertEquals(0,clubes.count());assertEquals(1,entrenadores.count());assertEquals(1,asociaciones.count());assertEquals(1,competiciones.count());assertEquals(0,sql.queryForObject("select count(*) from clubes_competiciones",Integer.class));
 }
 @Test void cascadaTambienFuncionaEnLaBaseDeDatos(){
  var c=club();servicio.guardarJugador(null,datosJugador(10,c.getId()));sql.update("delete from clubes where id=?",c.getId());assertEquals(0,jugadores.count());assertEquals(1,competiciones.count());assertEquals(0,sql.queryForObject("select count(*) from clubes_competiciones",Integer.class));
 }
 @Test void catalogosEnUsoNoSeEliminan(){
  var c=club();assertThrows(ReglaNegocio.class,()->servicio.eliminar(Entrenador.class,c.getEntrenador().getId()));assertThrows(ReglaNegocio.class,()->servicio.eliminar(Asociacion.class,c.getAsociacion().getId()));assertThrows(ReglaNegocio.class,()->servicio.eliminar(Competicion.class,c.getCompeticiones().getFirst().getId()));
 }
 @Test void oncePantallasSeRenderizanYFormularioJugadorSeValida() throws Exception {
  mvc.perform(get("/inicio")).andExpect(status().isOk()).andExpect(view().name("index"));
  for(String r:List.of("clubes","entrenadores","jugadores","asociaciones","competiciones")){
   mvc.perform(get("/web/"+r)).andExpect(status().isOk()).andExpect(view().name(r+"/listar"));
   mvc.perform(get("/web/"+r+"/nuevo")).andExpect(status().isOk()).andExpect(view().name(r+"/form"));
  }
  mvc.perform(post("/web/jugadores/guardar").param("nombre","").param("apellido","Pérez").param("numero","10").param("posicion","Portero")).andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("registro","nombre"));assertEquals(0,jugadores.count());
 }
 @Test void formularioJugadorAsignaClubYListadoMuestraNombre() throws Exception {
  var c=club();mvc.perform(post("/web/jugadores/guardar").param("nombre","Luis").param("apellido","Pérez").param("numero","10").param("posicion","Portero").param("clubId",c.getId().toString())).andExpect(status().is3xxRedirection());
  mvc.perform(get("/web/jugadores")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Club de prueba")));
  mvc.perform(get("/web/clubes/"+c.getId())).andExpect(status().isOk()).andExpect(view().name("clubes/listar"));
  var id=jugadores.findAll().getFirst().getId();mvc.perform(get("/web/jugadores/"+id+"/editar")).andExpect(status().isOk());
 }

 @Test void ejemplosSonCompletosYNoSeDuplican(){
  assertTrue(demo.cargar());assertEquals(2,clubes.count());assertEquals(8,jugadores.count());assertEquals(2,entrenadores.count());assertEquals(1,asociaciones.count());assertEquals(2,competiciones.count());
  var equipos=servicio.listar(Club.class);assertEquals(4,equipos.getFirst().getJugadores().size());assertEquals(equipos.getFirst().getAsociacion().getId(),equipos.getLast().getAsociacion().getId());assertFalse(demo.cargar());assertEquals(8,jugadores.count());
 }
 @Test void ejemplosNoAlteranUnaBaseConDatos(){
  var e=entrenador();assertFalse(demo.cargar());assertEquals(1,entrenadores.count());assertEquals(e.getId(),entrenadores.findAll().getFirst().getId());assertEquals(0,clubes.count());
 }
 @Test void selectorPosicionConservaValoresAnteriores() throws Exception {
  var j=datosJugador(10,null);j.setPosicion("Lateral");j=servicio.guardarJugador(null,j);
  var response=mvc.perform(get("/web/jugadores/"+j.getId()+"/editar")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  assertTrue(response.contains("value=\"Portero\""));assertTrue(response.contains("value=\"Defensa\""));assertTrue(response.contains("value=\"Mediocampista\""));assertTrue(response.contains("value=\"Delantero\""));assertTrue(response.contains("value=\"Lateral\""));
 }

 @Test void listadoEntrenadoresNoHaceConteosDelDashboard() throws Exception {
  var stats=factory.unwrap(org.hibernate.SessionFactory.class).getStatistics();stats.setStatisticsEnabled(true);stats.clear();
  try{mvc.perform(get("/web/entrenadores")).andExpect(status().isOk());assertEquals(1,stats.getPrepareStatementCount());}finally{stats.setStatisticsEnabled(false);}
 }
 @Test void selectorClubConsultaSoloNombresSinCargarRelaciones(){
  var club=club();var stats=factory.unwrap(org.hibernate.SessionFactory.class).getStatistics();stats.setStatisticsEnabled(true);stats.clear();
  try{assertEquals("Club de prueba",servicio.nombresClubes().get(club.getId()));assertEquals(1,stats.getPrepareStatementCount());}finally{stats.setStatisticsEnabled(false);}
 }
}
