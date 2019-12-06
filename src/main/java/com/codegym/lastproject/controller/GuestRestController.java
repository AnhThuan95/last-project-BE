package com.codegym.lastproject.controller;

import com.codegym.lastproject.message.request.SearchForm;
import com.codegym.lastproject.model.Comment;
import com.codegym.lastproject.model.House;
import com.codegym.lastproject.service.CommentService;
import com.codegym.lastproject.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/guest")
public class GuestRestController {
    @Autowired
    private HouseService houseService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/all")
    public ResponseEntity<?> getListHouse() {
        List<House> houses = houseService.findAll();
        if (houses.isEmpty()) {
            return new ResponseEntity<>("Chưa có nhà nào được tạo!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(houses, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/host/{id}")
    public ResponseEntity<?> getHostListHouse(@PathVariable("id") Long id) {
        List<House> houses = houseService.findByHostId(id);
        if (houses.isEmpty()) {
            return new ResponseEntity<>("Bạn chưa có nhà nào, vui lòng tạo nhà!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(houses, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getHouse(@PathVariable("id") Long id) {
        House house = houseService.findById(id);
        if (house == null) {
            return new ResponseEntity<>("Không tìm thấy kết quả phù hợp!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(house, HttpStatus.OK);
    }

    @PostMapping(value = "/search")
    public ResponseEntity<?> searchListHouse(@RequestBody SearchForm searchForm) {
        System.out.println(searchForm.getBedroomNumber());
        System.out.println(searchForm.getBathroomNumber());
        System.out.println(searchForm.getPrice());
        String address;
        if (searchForm.getAddress() == null) {
            address = "%%";
        } else {
            address = "%" + searchForm.getAddress() + "%";
        }
        System.out.println(address);

        List<House> houses = houseService.search(searchForm.getBedroomNumber(), searchForm.getBathroomNumber(), searchForm.getPrice(), address, searchForm.getBeginDate(), searchForm.getEndDate());
        System.out.println(houses.size());
        if (houses.size() == 0) {
            return new ResponseEntity<>("Không có kết quả tìm kiếm phù hơp!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(houses, HttpStatus.OK);
    }

    @GetMapping(value = "/comment/{id}")
    public ResponseEntity<?> getListComment(@PathVariable("id") Long id) {
        List<Comment> comments = commentService.findByHouseId(id);
        if (comments.size() == 0) {
            return new ResponseEntity<>("Chưa có đánh giá nào! Hãy trở thành người đầu tiên đánh giá.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
