package com.esteban.equipos.jpa;
import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.esteban.equipos.jpa.service.DatosDemo;
@Component
@ConditionalOnProperty(name="app.demo.enabled",havingValue="true",matchIfMissing=true)
public class DemoInicial implements ApplicationRunner {
 private final DatosDemo demo;public DemoInicial(DatosDemo demo){this.demo=demo;}
 @Override public void run(ApplicationArguments args){demo.cargar();}
}
