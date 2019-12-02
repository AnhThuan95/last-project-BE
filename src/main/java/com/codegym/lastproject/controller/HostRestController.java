package com.codegym.lastproject.controller;

import com.codegym.lastproject.model.*;
import com.codegym.lastproject.model.util.CategoryName;
import com.codegym.lastproject.model.util.StatusHouse;
import com.codegym.lastproject.model.util.StatusOrder;
import com.codegym.lastproject.security.service.UserDetailsServiceImpl;
import com.codegym.lastproject.service.*;
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

    @Autowired
    private OrderHouseService orderHouseService;

    @Autowired
    private OrderStatusService orderStatusService;

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

        originHouse.setImageUrls(house.getImageUrls());

        originHouse.setAddress(house.getAddress());
        originHouse.setArea(house.getArea());
        originHouse.setBathroomNumber(house.getBathroomNumber());
        originHouse.setBedroomNumber(house.getBedroomNumber());
        originHouse.setHouseName(house.getHouseName());
        originHouse.setPrice(house.getPrice());

        houseService.save(originHouse);

        setStatus();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/editHouse/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<House> editHouse(@PathVariable("id") Long id, @RequestBody House house) {
        User originUser = userDetailsService.getCurrentUser();
        House originHouse = houseService.findById(id);

        boolean isHost = isHost(originUser, originHouse);
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
        House house = houseService.findById(id);

        boolean isHost = isHost(originUser, house);
        if (house == null || !isHost) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        houseService.deleteHouse(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/listOrder/{id}")
    public ResponseEntity<List<OrderHouse>> getListOrder(@PathVariable("id") Long id) {
        User originUser = userDetailsService.getCurrentUser();
        House house = houseService.findById(id);

        boolean isHost = isHost(originUser, house);
        if (house == null || !isHost) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<OrderHouse> orderHouseList = orderHouseService.findByHouseId(id);
        if (orderHouseList == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(orderHouseList, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/house/{id}")
    public ResponseEntity<Void> setDoneOrder(@PathVariable("id") Long id) {
        User originUser = userDetailsService.getCurrentUser();
        OrderHouse orderHouse = orderHouseService.findById(id);
        House house = houseService.findById(orderHouse.getHouse().getId());

        boolean isHost = isHost(originUser, house);
        if (orderHouse == null || house == null || !isHost) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        orderHouse.setOrderStatus(orderStatusService.findByStatus(StatusOrder.DONE));
        orderHouseService.saveOrder(orderHouse);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean isHost(User user, House originHouse) {
        List<House> houses = houseService.findByHostId(user.getId());
        boolean isHost = houses.contains(originHouse);
        return isHost;
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
