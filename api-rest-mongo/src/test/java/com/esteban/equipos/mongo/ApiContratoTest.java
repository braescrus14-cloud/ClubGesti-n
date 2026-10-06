package com.esteban.equipos.mongo;
import com.esteban.equipos.mongo.controller.*;
import com.esteban.equipos.mongo.model.*;
import com.esteban.equipos.mongo.service.*;
import org.junit.jupiter.api.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ApiContratoTest {
 EquiposServicio service; MockMvc mvc;
 @BeforeEach void preparar(){
  service=mock(EquiposServicio.class);
  mvc=MockMvcBuilders.standaloneSetup(new ClubRestControlador(service),new EntrenadorRestControlador(service),new JugadorRestControlador(service),new AsociacionRestControlador(service),new CompeticionRestControlador(service)).setControllerAdvice(new ApiErrores()).build();
 }
 @Test void losCincoContratosExponenListados() throws Exception {
  for(String recurso:new String[]{"clubes","entrenadores","jugadores","asociaciones","competiciones"})mvc.perform(get("/api/"+recurso)).andExpect(status().isOk());
 }
 @Test void crearResponde201YUbicacion() throws Exception {
  Entrenador e=new Entrenador();e.setId(7L);e.setNombre("Ana");
  when(service.guardarCatalogo(eq(Entrenador.class),isNull(),any(Entrenador.class))).thenReturn(e);
  mvc.perform(post("/api/entrenadores").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Ana\",\"apellido\":\"Pérez\",\"edad\":40,\"nacionalidad\":\"Colombiana\"}"))
   .andExpect(status().isCreated()).andExpect(header().string("Location","/api/entrenadores/7")).andExpect(jsonPath("$.id").value(7));
 }
 @Test void validacionEnInterfazJavaSeAplica() throws Exception {
  mvc.perform(post("/api/entrenadores").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"\",\"apellido\":\"Pérez\",\"edad\":2,\"nacionalidad\":\"CO\"}"))
   .andExpect(status().isBadRequest()).andExpect(jsonPath("$.detail").isNotEmpty());verifyNoInteractions(service);
 }
 @Test void fechasInvalidasSon400() throws Exception {
  mvc.perform(post("/api/competiciones").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Copa\",\"montoPremio\":10,\"fechaInicio\":\"2026-10-10\",\"fechaFin\":\"2026-10-01\"}"))
   .andExpect(status().isBadRequest());verifyNoInteractions(service);
 }
 @Test void clubRequiereSusRelacionesObligatorias() throws Exception {
  mvc.perform(post("/api/clubes").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Club\"}"))
   .andExpect(status().isBadRequest());verifyNoInteractions(service);
 }
 @Test void inexistenteResponde404() throws Exception {
  when(service.buscar(Club.class,99L)).thenThrow(new ReglaNegocio(404,"Club no encontrado"));
  mvc.perform(get("/api/clubes/99")).andExpect(status().isNotFound());
 }
 @Test void restriccionResponde409() throws Exception {
  doThrow(new ReglaNegocio(409,"Registro en uso")).when(service).eliminar(Asociacion.class,1L);
  mvc.perform(delete("/api/asociaciones/1")).andExpect(status().isConflict()).andExpect(jsonPath("$.detail").value("Registro en uso"));
 }
 @Test void eliminarResponde204() throws Exception {
  mvc.perform(delete("/api/jugadores/8")).andExpect(status().isNoContent());verify(service).eliminar(Jugador.class,8L);
 }
 @Test void idMalFormadoResponde400() throws Exception {mvc.perform(get("/api/clubes/abc")).andExpect(status().isBadRequest());}
}
