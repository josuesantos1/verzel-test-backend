package com.verzel.motors.controllers;

import com.verzel.motors.Services.S3Service;
import com.verzel.motors.database.CarsModel;
import com.verzel.motors.database.CarsRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public String upload(@RequestPart MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String filename = "cars/" + UUID.randomUUID() + "-" + originalFilename;

        try {
            return s3Service.uploadFile(filename, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
