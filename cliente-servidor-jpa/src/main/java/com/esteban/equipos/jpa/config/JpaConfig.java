package com.esteban.equipos.jpa.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import jakarta.persistence.EntityManagerFactory;
@Configuration
public class JpaConfig {
 @Bean public JpaTransactionManager jpaTransactionManager(EntityManagerFactory factory){return new JpaTransactionManager(factory);}
}
