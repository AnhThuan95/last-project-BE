package com.codegym.lastproject.controller;

import com.codegym.lastproject.model.House;
import com.codegym.lastproject.security.service.UserDetailsServiceImpl;
import com.codegym.lastproject.service.CategoryService;
import com.codegym.lastproject.service.HouseService;
import com.codegym.lastproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/guest")
public class GuestRestController {
    @Autowired
    private HouseService houseService;

    @GetMapping("/all")
    public ResponseEntity<List<House>> getListHouse() {
        List<House> houses = houseService.findAll();
        if (houses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(houses, HttpStatus.OK);
    }

    @GetMapping(value = "/host/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<House>> getHostListHouse(@PathVariable("id") Long id) {
        List<House> houses = houseService.findByHostId(id);
        if (houses.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(houses, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<House> getHouse(@PathVariable("id") Long id) {
        House house = houseService.findById(id);
        if (house == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(house, HttpStatus.OK);
    }
}
