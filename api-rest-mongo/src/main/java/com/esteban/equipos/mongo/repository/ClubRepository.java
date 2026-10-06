package com.esteban.equipos.mongo.repository;
import com.esteban.equipos.mongo.model.Club;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ClubRepository extends MongoRepository<Club,Long> { }
