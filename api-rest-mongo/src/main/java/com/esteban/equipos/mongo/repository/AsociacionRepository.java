package com.esteban.equipos.mongo.repository;
import com.esteban.equipos.mongo.model.Asociacion;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface AsociacionRepository extends MongoRepository<Asociacion,Long> { }
