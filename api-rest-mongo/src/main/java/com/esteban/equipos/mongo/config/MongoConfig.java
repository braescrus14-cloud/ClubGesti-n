package com.esteban.equipos.mongo.config;
import org.springframework.context.annotation.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort;
import com.esteban.equipos.mongo.model.*;
@Configuration
public class MongoConfig {
 @Bean MongoTransactionManager transactionManager(MongoDatabaseFactory factory){return new MongoTransactionManager(factory);}
 @Bean ApplicationRunner prepararMongo(MongoTemplate mongo){return args -> {
  for(String c: new String[]{"clubes","entrenadores","jugadores","asociaciones","competiciones","control"})
   if(!mongo.collectionExists(c))mongo.createCollection(c);
  // Un documento compartido serializa las escrituras transaccionales para impedir
  // carreras entre la asignación de referencias y la eliminación de sus destinos.
  mongo.upsert(Query.query(Criteria.where("_id").is("escritura")),new Update().setOnInsert("valor",0L),"control");
  mongo.indexOps(Club.class).ensureIndex(new Index().on("entrenador",Sort.Direction.ASC).unique().named("entrenador_exclusivo"));
 };}
}
