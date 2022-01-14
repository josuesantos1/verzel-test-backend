package com.verzel.motors.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarsRepository extends JpaRepository<CarsModel, Long> {
    public Optional<CarsModel> findByName(String name);
    public List<CarsModel> findAllByOwner(String owner);
}
