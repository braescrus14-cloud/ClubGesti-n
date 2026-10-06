package com.esteban.equipos.mongo.repository;
import com.esteban.equipos.mongo.model.Competicion;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface CompeticionRepository extends MongoRepository<Competicion,Long> { }
