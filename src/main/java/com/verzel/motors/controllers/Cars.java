package com.verzel.motors.controllers;

import com.verzel.motors.Services.S3Service;
import com.verzel.motors.database.CarsModel;
import com.verzel.motors.database.CarsRepository;
import com.verzel.motors.database.Templates.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/cars")
public class Cars {
    private CarsRepository carsRepository;
    private S3Service s3Service;

    @Autowired
    public Cars(CarsRepository carsRepository, S3Service s3Service) {
        this.carsRepository = carsRepository;
        this.s3Service = s3Service;
    }

    @GetMapping()
    @CrossOrigin()
    public List<CarsModel> listCars() {
        List<CarsModel> cars = carsRepository.findAll();
        for (CarsModel car: cars) {
            String img = car.getImage();
            URL url = s3Service.getPresignedUrl(img);
            car.setImage(url.toString());
        }

        return cars;
    }

    @CrossOrigin()
    @PatchMapping("/me")
    public CarsModel update(@RequestBody CarsModel cars) {
        Optional<CarsModel> carsModel = carsRepository.findById(cars.getId());
        Object email = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        cars.setImage(carsModel.get().getImage());
        cars.setOwner((String) email);

        carsRepository.save(cars);
        return cars;
    }

    @DeleteMapping("/me")
    public void delete(@RequestParam Long id) {
        carsRepository.deleteById(id);
        return;
    }

    @CrossOrigin()
    @PostMapping("/me")
    public CarsModel create(@RequestBody CarsModel cars) {
        CarsModel carsModel = carsRepository.findByImage(cars.getImage());
        Object email = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        cars.setImage(carsModel.getImage());
        cars.setOwner((String) email);
        cars.setId(carsModel.getId());

        carsRepository.save(cars);
        return cars;
    }

    @GetMapping("/me")
    public List<CarsModel> listCarsByOwner() {
        Object email = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        List<CarsModel> cars = carsRepository.findAllByOwner((String) email);
        for (CarsModel car: cars) {
            String img = car.getImage();
            URL url = s3Service.getPresignedUrl(img);
            car.setImage(url.toString());
        }

        return cars;
    }

    @CrossOrigin()
    @PostMapping("/me/upload")
    public Upload upload(@RequestParam String file) {
        Object email = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        CarsModel carsModel = new CarsModel();
        String filename = UUID.randomUUID() + file;

        carsModel.setOwner((String) email);
        carsModel.setImage(filename);

        carsRepository.save(carsModel);

        String url = s3Service.putPresignedUrl(filename).toString();
        Upload upload = new Upload(url, filename);

        return upload;
    }

    @PostMapping("/me/upload/update")
    public Upload uploadPatch(@RequestParam String file, @RequestParam String id) {
        Object email = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        CarsModel carsModel = new CarsModel();
        String filename = UUID.randomUUID() + file;

        carsModel.setOwner((String) email);
        carsModel.setImage(filename);
        carsModel.setId(Long.valueOf(id));

        carsRepository.save(carsModel);

        String url = s3Service.putPresignedUrl(filename).toString();
        Upload upload = new Upload(url, filename);
        return upload;
    }
}
