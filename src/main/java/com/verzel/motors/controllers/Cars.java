package com.verzel.motors.controllers;

import com.verzel.motors.Services.S3Service;
import com.verzel.motors.database.CarsModel;
import com.verzel.motors.database.CarsRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        return carsRepository.findAll();
    }

    @PatchMapping("/me")
    public CarsModel update(@RequestBody CarsModel cars) {
        return carsRepository.save(cars);
    }

    @DeleteMapping("/me")
    public void delete(@RequestParam Long id) {
        carsRepository.deleteById(id);
        return;
    }

    @PostMapping("/me/upload")
    public URL upload(@RequestBody String file) {
        String filename = UUID.randomUUID() +"-"+ file;
        return s3Service.putPresignedUrl(filename);
    }

    @GetMapping("/me/upload")
    public URL viewImage(@RequestParam String file) {
        return s3Service.getPresignedUrl(file);
    }

    @PostMapping("/me")
    public CarsModel create(@RequestBody CarsModel car) {
        return carsRepository.save(car);
    }

    @GetMapping("/me")
    public List<CarsModel> listCarsByOwner(@RequestParam String owner) {
        return carsRepository.findAllByOwner(owner);
    }
}
