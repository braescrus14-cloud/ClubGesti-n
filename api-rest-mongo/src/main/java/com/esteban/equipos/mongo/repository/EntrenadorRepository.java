package com.esteban.equipos.mongo.repository;
import com.esteban.equipos.mongo.model.Entrenador;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface EntrenadorRepository extends MongoRepository<Entrenador,Long> { }
