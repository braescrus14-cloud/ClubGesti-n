package com.esteban.equipos.unificada;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
@SpringBootApplication
@ComponentScan(basePackages={"com.esteban.equipos.jpa","com.esteban.equipos.mongo","com.esteban.equipos.unificada"},nameGenerator=FullyQualifiedAnnotationBeanNameGenerator.class,
 excludeFilters=@ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE,classes={com.esteban.equipos.jpa.RelacionesApplication.class,com.esteban.equipos.mongo.ApiMongoApplication.class}))
@EntityScan("com.esteban.equipos.jpa.model")
@EnableJpaRepositories(basePackages="com.esteban.equipos.jpa.repository",transactionManagerRef="jpaTransactionManager",nameGenerator=FullyQualifiedAnnotationBeanNameGenerator.class)
@EnableMongoRepositories(basePackages="com.esteban.equipos.mongo.repository",nameGenerator=FullyQualifiedAnnotationBeanNameGenerator.class)
public class GestionUnificadaApplication {
 public static void main(String[] args){SpringApplication.run(GestionUnificadaApplication.class,args);}
}
