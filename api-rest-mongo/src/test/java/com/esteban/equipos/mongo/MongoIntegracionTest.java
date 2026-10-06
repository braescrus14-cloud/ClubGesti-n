package com.esteban.equipos.mongo;
import com.esteban.equipos.mongo.model.*;
import com.esteban.equipos.mongo.dto.*;
import com.esteban.equipos.mongo.service.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnabledIfEnvironmentVariable(named="PRUEBAS_MONGO_URI",matches=".+")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoIntegracionTest {
 static final String DB="equipos_test_"+UUID.randomUUID().toString().replace("-","");
 @DynamicPropertySource static void propiedades(DynamicPropertyRegistry r){r.add("spring.mongodb.uri",()->System.getenv("PRUEBAS_MONGO_URI"));r.add("spring.mongodb.database",()->DB);}
 @Autowired EquiposServicio servicio;
 @Autowired MongoTemplate mongo;
 @Autowired WebApplicationContext context;
 MockMvc mvc;
 @BeforeEach void limpiar(){for(String c:List.of("clubes","entrenadores","jugadores","asociaciones","competiciones"))mongo.remove(new Query(),c);mvc=MockMvcBuilders.webAppContextSetup(context).build();}
 @AfterAll void limpiarBaseTemporal(){assertTrue(mongo.getDb().getName().startsWith("equipos_test_"));mongo.getDb().drop();}
 Entrenador entrenador(){Entrenador x=new Entrenador();x.setNombre("Ana");x.setApellido("Pérez");x.setEdad(40);x.setNacionalidad("Colombiana");return servicio.guardarCatalogo(Entrenador.class,null,x);}
 Jugador jugador(int numero){Jugador x=new Jugador();x.setNombre("Luis");x.setApellido("Díaz");x.setNumero(numero);x.setPosicion("Delantero");return servicio.guardarCatalogo(Jugador.class,null,x);}
 Asociacion asociacion(){Asociacion x=new Asociacion();x.setNombre("Federación Colombiana");x.setSiglas("FCF");x.setPais("Colombia");x.setPresidente("Presidente de ejemplo");return servicio.guardarCatalogo(Asociacion.class,null,x);}
 Competicion competicion(){Competicion x=new Competicion();x.setNombre("Copa de prueba");x.setMontoPremio(1000000);x.setFechaInicio(LocalDate.of(2026,10,1));x.setFechaFin(LocalDate.of(2026,11,1));return servicio.guardarCatalogo(Competicion.class,null,x);}
 ClubFormulario formulario(Entrenador e,Asociacion a,Jugador j,Competicion c){ClubFormulario f=new ClubFormulario();f.setNombre("Club prueba");f.setEntrenadorId(e.getId());f.setAsociacionId(a.getId());f.setJugadorIds(List.of(j.getId()));f.setCompeticionIds(List.of(c.getId()));return f;}
 @Test void relacionesSeGuardanComoReferenciasYSeResuelven(){
  var e=entrenador();var a=asociacion();var j=jugador(10);var c=competicion();var club=servicio.guardarClub(null,formulario(e,a,j,c));
  Document raw=mongo.getCollection("clubes").find(new Document("_id",club.getId())).first();assertNotNull(raw);assertEquals(e.getId(),((Number)raw.get("entrenador")).longValue());assertInstanceOf(Number.class,((List<?>)raw.get("jugadores")).getFirst());
  var loaded=servicio.buscar(Club.class,club.getId());assertEquals("Ana",loaded.getEntrenador().getNombre());assertEquals(10,loaded.getJugadores().getFirst().getNumero());assertEquals(c.getId(),loaded.getCompeticiones().getFirst().getId());
 }
 @Test void entrenadorYJugadorSonExclusivos(){
  var e=entrenador();var a=asociacion();var j=jugador(10);var c=competicion();servicio.guardarClub(null,formulario(e,a,j,c));
  assertEquals(409,assertThrows(ReglaNegocio.class,()->servicio.guardarClub(null,formulario(e,a,jugador(11),c))).getEstado());
  var e2=entrenador();assertEquals(409,assertThrows(ReglaNegocio.class,()->servicio.guardarClub(null,formulario(e2,a,j,c))).getEstado());
 }
 @Test void asociacionYCompeticionSeComparten(){
  var a=asociacion();var c=competicion();servicio.guardarClub(null,formulario(entrenador(),a,jugador(10),c));servicio.guardarClub(null,formulario(entrenador(),a,jugador(10),c));assertEquals(2,servicio.listar(Club.class).size());
 }
 @Test void eliminacionProtegeReferenciasYPermiteLiberarlas(){
  var e=entrenador();var a=asociacion();var j=jugador(10);var c=competicion();var club=servicio.guardarClub(null,formulario(e,a,j,c));
  assertThrows(ReglaNegocio.class,()->servicio.eliminar(Entrenador.class,e.getId()));assertThrows(ReglaNegocio.class,()->servicio.eliminar(Jugador.class,j.getId()));assertThrows(ReglaNegocio.class,()->servicio.eliminar(Asociacion.class,a.getId()));assertThrows(ReglaNegocio.class,()->servicio.eliminar(Competicion.class,c.getId()));
  servicio.eliminar(Club.class,club.getId());servicio.eliminar(Entrenador.class,e.getId());servicio.eliminar(Jugador.class,j.getId());servicio.eliminar(Asociacion.class,a.getId());servicio.eliminar(Competicion.class,c.getId());assertTrue(servicio.listar(Club.class).isEmpty());
 }
 @Test void dorsalesDuplicadosYReferenciasInexistentesSeRechazan(){
  var e=entrenador();var a=asociacion();var j=jugador(10);var c=competicion();var f=formulario(e,a,j,c);var j2=jugador(10);f.setJugadorIds(List.of(j.getId(),j2.getId()));assertThrows(ReglaNegocio.class,()->servicio.guardarClub(null,f));
  f.setJugadorIds(List.of(999999L));assertEquals(404,assertThrows(ReglaNegocio.class,()->servicio.guardarClub(null,f)).getEstado());assertTrue(servicio.listar(Club.class).isEmpty());
 }
 @Test void actualizarJugadorNoDuplicaDorsal(){
  var j=jugador(10);var j2=jugador(11);var f=formulario(entrenador(),asociacion(),j,competicion());f.setJugadorIds(List.of(j.getId(),j2.getId()));servicio.guardarClub(null,f);j2.setNumero(10);
  assertThrows(ReglaNegocio.class,()->servicio.guardarCatalogo(Jugador.class,j2.getId(),j2));assertEquals(11,servicio.buscar(Jugador.class,j2.getId()).getNumero());
 }
 @Test void actualizarClubRetiraRelaciones(){
  var j=jugador(10);var f=formulario(entrenador(),asociacion(),j,competicion());var club=servicio.guardarClub(null,f);f.setJugadorIds(List.of());f.setCompeticionIds(List.of());f.setNombre("Renombrado");servicio.guardarClub(club.getId(),f);assertTrue(servicio.buscar(Club.class,club.getId()).getJugadores().isEmpty());servicio.eliminar(Jugador.class,j.getId());
 }
 @Test void carrerasNoPermitenCompartirEntrenador() throws Exception {
  var e=entrenador();var a=asociacion();var c=competicion();var f1=formulario(e,a,jugador(1),c);var f2=formulario(e,a,jugador(2),c);var gate=new CountDownLatch(1);
  try(var pool=Executors.newFixedThreadPool(2)){
   Callable<Boolean> t1=()->{gate.await();try{servicio.guardarClub(null,f1);return true;}catch(RuntimeException ex){return false;}};
   Callable<Boolean> t2=()->{gate.await();try{servicio.guardarClub(null,f2);return true;}catch(RuntimeException ex){return false;}};
   var r1=pool.submit(t1);var r2=pool.submit(t2);gate.countDown();assertNotEquals(r1.get(20,TimeUnit.SECONDS),r2.get(20,TimeUnit.SECONDS));assertEquals(1,servicio.listar(Club.class).size());
  }
 }
}
