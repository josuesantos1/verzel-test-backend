package com.verzel.motors.controllers;

import com.verzel.motors.Services.S3Service;
import com.verzel.motors.database.CarsModel;
import com.verzel.motors.database.CarsRepository;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cars")
public class Cars {
    private final CarsRepository carsRepository;
    private final S3Service s3Service;

    public Cars(CarsRepository carsRepository, S3Service s3Service) {
        this.carsRepository = carsRepository;
        this.s3Service = s3Service;
    }

    @GetMapping()
    public List<CarsModel> listCars() {
        List<CarsModel> cars = carsRepository.findAll();
        for (CarsModel car: cars) {
            String img = car.getImage();
            URL url = s3Service.getPresignedUrl(img);
            car.setImage(url.toString());
        }

        return cars;
    }

    @PatchMapping("/me")
    public URL update(@RequestBody CarsModel cars) {
        String filename = UUID.randomUUID() +"-"+ cars.getImage();
        cars.setImage(filename);

        carsRepository.save(cars);
        return s3Service.putPresignedUrl(filename);
    }

    @DeleteMapping("/me")
    public void delete(@RequestParam Long id) {
        carsRepository.deleteById(id);
        return;
    }

    @PostMapping("/me")
    public URL create(@RequestBody CarsModel cars) {
        String filename = UUID.randomUUID() +"-"+ cars.getImage();
        cars.setImage(filename);
        carsRepository.save(cars);
        return s3Service.putPresignedUrl(filename);
    }

    @GetMapping("/me")
    public List<CarsModel> listCarsByOwner(@RequestParam String owner) {
        List<CarsModel> cars = carsRepository.findAllByOwner(owner);
        for (CarsModel car: cars) {
            String img = car.getImage();
            URL url = s3Service.getPresignedUrl(img);
            car.setImage(url.toString());
        }

        return cars;
    }
}
