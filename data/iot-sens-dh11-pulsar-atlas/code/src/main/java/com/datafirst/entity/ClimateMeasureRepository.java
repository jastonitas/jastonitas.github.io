package com.datafirst.entity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClimateMeasureRepository extends MongoRepository<ClimateMeasure, String> {
    ClimateMeasure findById(long id);
}
