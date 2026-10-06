package com.esteban.equipos.jpa;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@org.springframework.data.jpa.repository.config.EnableJpaRepositories(basePackages="com.esteban.equipos.jpa.repository",transactionManagerRef="jpaTransactionManager")
@SpringBootApplication
public class RelacionesApplication {
 public static void main(String[] args){SpringApplication.run(RelacionesApplication.class,args);}
 @org.springframework.context.annotation.Bean
 public org.springframework.web.servlet.config.annotation.WebMvcConfigurer entradaStandalone(){
  return new org.springframework.web.servlet.config.annotation.WebMvcConfigurer(){
   @Override public void addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry registry){registry.addViewController("/").setViewName("redirect:/inicio");}
  };
 }
}
