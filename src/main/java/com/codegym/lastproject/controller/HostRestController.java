package com.codegym.lastproject.controller;

import com.codegym.lastproject.message.request.MonthYearForm;
import com.codegym.lastproject.model.*;
import com.codegym.lastproject.model.util.CategoryName;
import com.codegym.lastproject.model.util.StatusOrder;
import com.codegym.lastproject.security.service.UserDetailsServiceImpl;
import com.codegym.lastproject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private OrderHouseService orderHouseService;

    @Autowired
    private OrderStatusService orderStatusService;

    @PreAuthorize("hasRole('HOST')")
    @PostMapping("/createHouse")
    public ResponseEntity<String> createHouse(@RequestBody House house) {
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

        houseStatusService.setStatusNewHouse();

        return new ResponseEntity<>("Tạo nhà thành công!", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('HOST')")
    @PutMapping(value = "/editHouse/{id}")
    public ResponseEntity<?> editHouse(@PathVariable("id") Long id, @RequestBody House house) {
        User originUser = userDetailsService.getCurrentUser();
        House originHouse = houseService.findById(id);

        boolean isHost = houseService.isHost(originUser, originHouse);
        if (originHouse == null) {
            return new ResponseEntity<>("Nhà này không tồn tại!", HttpStatus.NOT_FOUND);
        }
        if (!isHost) {
            return new ResponseEntity<>("Bạn không có quyền thay đổi nhà này!", HttpStatus.NOT_FOUND);
        }

        originHouse.setAddress(house.getAddress());
        originHouse.setArea(house.getArea());
        originHouse.setBathroomNumber(house.getBathroomNumber());
        originHouse.setBedroomNumber(house.getBedroomNumber());
        originHouse.setHouseName(house.getHouseName());
        originHouse.setPrice(house.getPrice());

        if (house.getImageUrls() != null) {
            originHouse.setImageUrls(house.getImageUrls());
        }

        houseService.save(originHouse);
        return new ResponseEntity<>(originHouse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('HOST')")
    @DeleteMapping(value = "/deleteHouse/{id}")
    public ResponseEntity<String> deleteHouse(@PathVariable("id") Long id) {
        User originUser = userDetailsService.getCurrentUser();
        House house = houseService.findById(id);

        boolean isHost = houseService.isHost(originUser, house);
        if (house == null) {
            return new ResponseEntity<>("Nhà này không tồn tại!", HttpStatus.NOT_FOUND);
        }
        if (!isHost) {
            return new ResponseEntity<>("Bạn không có quyền xem nhà này!", HttpStatus.NOT_FOUND);
        }

        houseService.deleteHouse(id);
        return new ResponseEntity<>("Đã xóa thành công!", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('HOST')")
    @GetMapping(value = "/listOrder/{id}")
    public ResponseEntity<?> getListOrder(@PathVariable("id") Long id) {
        User originUser = userDetailsService.getCurrentUser();
        House house = houseService.findById(id);

        List<OrderHouse> orderHouseList = orderHouseService.findByHouseId(id);

        boolean isHost = houseService.isHost(originUser, house);
        if (house == null) {
            return new ResponseEntity<>("Nhà này không tồn tại!", HttpStatus.NOT_FOUND);
        }
        if (!isHost) {
            return new ResponseEntity<>("Bạn không có quyền xem nhà này!", HttpStatus.NOT_FOUND);
        }
        if(orderHouseList.size() == 0) {
            return new ResponseEntity<>("Nhà này không có yêu cầu nào!", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(orderHouseList, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('HOST')")
    @PutMapping(value = "/done/{id}")
    public ResponseEntity<String> setDoneOrder(@PathVariable("id") Long id) {
        OrderHouse orderHouse = orderHouseService.findById(id);
        boolean isConformity = houseService.isConformity(orderHouse);
        if (isConformity) {
            return new ResponseEntity<>("Order này không tồn tại hoặc đã xử lý.", HttpStatus.NOT_FOUND);
        }

        orderHouse.setOrderStatus(orderStatusService.findByStatus(StatusOrder.DONE));
        orderHouseService.saveOrder(orderHouse);

        return new ResponseEntity<>("Yêu cầu đã được chấp nhận thành công!", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('HOST')")
    @PostMapping(value = "/income/{id}")
    public ResponseEntity<String> calculateIncomeByMonth(@PathVariable("id") Long id, @RequestBody MonthYearForm monthYearForm) {
        User originUser = userDetailsService.getCurrentUser();
        House house = houseService.findById(id);

        boolean isHost = houseService.isHost(originUser, house);
        if (house == null) {
            return new ResponseEntity<>("Nhà này không tồn tại!", HttpStatus.NOT_FOUND);
        }
        if (!isHost) {
            return new ResponseEntity<>("Bạn không có quyền xem nhà này!", HttpStatus.NOT_FOUND);
        }

        int month = monthYearForm.getMonth();
        int year = monthYearForm.getYear() - 1900;

        Date begin = new Date(year, (month - 1), 1);
        Date end = new Date(year, month, 1);
        List<HouseStatus> houseStatusList = houseStatusService.findHouseStatusInMonth(begin, end, id);

        long days = 0L;
        for (HouseStatus houseStatus : houseStatusList) {
            days += (houseStatus.getEndDate().getTime() - houseStatus.getBeginDate().getTime()) / 86400000 + 1;
        }

        long income = days * house.getPrice();
        return new ResponseEntity<>("Thu nhập tháng " + monthYearForm.getMonth() + " năm " + monthYearForm.getYear() + " của nhà này là: " + income + " VND!", HttpStatus.OK);
    }
}
