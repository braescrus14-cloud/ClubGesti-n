package com.esteban.equipos.unificada;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.esteban.equipos.jpa.service.DatosDemo;
import com.esteban.equipos.jpa.repository.ClubRepository;
import com.esteban.equipos.mongo.service.EquiposServicio;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@EnabledIfEnvironmentVariable(named="PRUEBAS_MONGO_URI",matches=".+")
@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:puerto_unico;DB_CLOSE_DELAY=-1","spring.jpa.hibernate.ddl-auto=create-drop","app.demo.enabled=false"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PuertoUnicoTest {
 static final String DB="equipos_test_unificado_"+UUID.randomUUID().toString().replace("-","");
 @DynamicPropertySource static void propiedades(DynamicPropertyRegistry r){r.add("spring.mongodb.uri",()->System.getenv("PRUEBAS_MONGO_URI"));r.add("spring.mongodb.database",()->DB);}
 @Autowired WebApplicationContext context;@Autowired MongoTemplate mongo;@Autowired DatosDemo demo;@Autowired ClubRepository clubes;@Autowired EquiposServicio api;
 MockMvc mvc;
 @BeforeAll void preparar(){mvc=MockMvcBuilders.webAppContextSetup(context).build();}
 @AfterAll void limpiar(){assertTrue(mongo.getDb().getName().startsWith("equipos_test_unificado_"));mongo.getDb().drop();}
 @Test void ambasInterfacesYApiEnElMismoContexto() throws Exception {
  mvc.perform(get("/")).andExpect(status().isOk()).andExpect(forwardedUrl("/rest.html"));
  mvc.perform(get("/inicio")).andExpect(status().isOk()).andExpect(view().name("index"));
  mvc.perform(get("/rest.html")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("/web/clubes")));
  mvc.perform(get("/api/clubes")).andExpect(status().isOk());
  mvc.perform(get("/web/jugadores/nuevo")).andExpect(status().isOk());
 }
 @Test void gestoresDeTransaccionesMantienenBasesIndependientes(){
  assertTrue(demo.cargar());assertEquals(2,clubes.count());
  var a=new com.esteban.equipos.mongo.model.Asociacion();a.setNombre("Federación de prueba unificada");a.setSiglas("FPU");a.setPais("Colombia");a.setPresidente("Ana Rojas");a=api.guardarCatalogo(com.esteban.equipos.mongo.model.Asociacion.class,null,a);
  assertNotNull(a.getId());assertEquals(2,clubes.count());assertTrue(api.listar(com.esteban.equipos.mongo.model.Club.class).isEmpty());
 }
 @Test void enlacesNoApuntanA8093() throws Exception {
  mvc.perform(get("/inicio")).andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("8093"))));
  mvc.perform(get("/rest.html")).andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("8093"))));
 }
}
