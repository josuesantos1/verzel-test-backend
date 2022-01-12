package com.verzel.motors.controllers;

import com.verzel.motors.database.CarsModel;
import com.verzel.motors.database.CarsRepository;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class Cars {
    private final CarsRepository carsRepository;

    public Cars(CarsRepository carsRepository) {
        this.carsRepository = carsRepository;
    }

    @GetMapping()
    public List<CarsModel> listCars() {
        return carsRepository.findAll();
    }

    @PatchMapping
    public CarsModel update(@RequestBody CarsModel cars) {
        return carsRepository.save(cars);
    }

    @DeleteMapping
    public void delete(@RequestParam Long id) {
        carsRepository.deleteById(id);
        return;
    }

    @PostMapping()
    public CarsModel create(@RequestBody CarsModel car) {
        return carsRepository.save(car);
    }

}
