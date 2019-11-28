package com.codegym.lastproject.controller;

import com.codegym.lastproject.model.*;
import com.codegym.lastproject.model.util.CategoryName;
import com.codegym.lastproject.model.util.StatusHouse;
import com.codegym.lastproject.security.service.UserDetailsServiceImpl;
import com.codegym.lastproject.service.CategoryService;
import com.codegym.lastproject.service.HouseService;
import com.codegym.lastproject.service.HouseStatusService;
import com.codegym.lastproject.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/host")
public class HostRestController {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private HouseStatusService houseStatusService;

    @Autowired
    private StatusService statusService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/createHouse")
    public ResponseEntity<House> createHouse(@RequestBody House house) {
        User originUser = userDetailsService.getCurrentUser();
        House originHouse = new House();
        originHouse.setUser(originUser);

        if (house.getCategory() != null) {
            CategoryName category = house.getCategory().getName();
            Category originCategory = categoryService.findByName(category);
            originHouse.setCategory(originCategory);
        }

        if (house.getImageUrls() != null) {
            originHouse.setImageUrls(house.getImageUrls());
        }

        originHouse.setAddress(house.getAddress());
        originHouse.setArea(house.getArea());
        originHouse.setBathroomNumber(house.getBathroomNumber());
        originHouse.setBedroomNumber(house.getBedroomNumber());
        originHouse.setHouseName(house.getHouseName());
        originHouse.setPrice(house.getPrice());
        originHouse.setOrderHouses(null);

        houseService.save(originHouse);

        setStatus();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/editHouse/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<House> editHouse(@PathVariable("id") Long id, @RequestBody House house) {
        User originUser = userDetailsService.getCurrentUser();
        List<House> houses = houseService.findByHostId(originUser.getId());
        for (House house1: houses) {
            System.out.println(house1.getId());
        }
        House originHouse = houseService.findById(id);
        boolean isHost = houses.contains(originHouse);
        System.out.println(isHost);
        if (originHouse == null || !isHost) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        originHouse.setAddress(house.getAddress());
        originHouse.setArea(house.getArea());
        originHouse.setBathroomNumber(house.getBathroomNumber());
        originHouse.setBedroomNumber(house.getBedroomNumber());
        originHouse.setHouseName(house.getHouseName());
        originHouse.setPrice(house.getPrice());

        houseService.save(originHouse);
        return new ResponseEntity<>(originHouse, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/deleteHouse/{id}")
    public ResponseEntity<Void> deleteHouse(@PathVariable("id") Long id) {
        User originUser = userDetailsService.getCurrentUser();
        List<House> houses = houseService.findByHostId(originUser.getId());
        House house = houseService.findById(id);
        boolean isHost = houses.contains(house);
        if (house == null || !isHost) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        houseService.deleteHouse(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void setStatus() {
        HouseStatus houseStatus = new HouseStatus();

        Long id = houseService.findMaxHouseId();
        House house = houseService.findById(id);
        System.out.println(house.getId());
        houseStatus.setHouse(house);

        Date beginDate = new Date(System.currentTimeMillis());
        houseStatus.setBeginDate(beginDate);

        Date endDate = new Date(System.currentTimeMillis() + 7776000000L);
        houseStatus.setEndDate(endDate);

        Status status = statusService.findByStatus(StatusHouse.AVAILABLE);
        houseStatus.setStatus(status);

        houseStatusService.save(houseStatus);
    }
}
