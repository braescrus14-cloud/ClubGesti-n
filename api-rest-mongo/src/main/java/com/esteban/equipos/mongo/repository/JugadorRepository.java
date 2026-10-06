package com.esteban.equipos.mongo.repository;
import com.esteban.equipos.mongo.model.Jugador;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface JugadorRepository extends MongoRepository<Jugador,Long> { }
