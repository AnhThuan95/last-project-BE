package com.codegym.lastproject.controller;

import com.codegym.lastproject.model.House;
import com.codegym.lastproject.model.HouseStatus;
import com.codegym.lastproject.model.Status;
import com.codegym.lastproject.model.User;
import com.codegym.lastproject.model.util.StatusHouse;
import com.codegym.lastproject.security.service.UserDetailsServiceImpl;
import com.codegym.lastproject.service.HouseService;
import com.codegym.lastproject.service.HouseStatusService;
import com.codegym.lastproject.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/status")
public class HouseStatusRestController {
    @Autowired
    private StatusService statusService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private HouseStatusService houseStatusService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getListHouse(@PathVariable("id") Long id) {
        List<HouseStatus> houseStatuses = houseStatusService.findAllByHouseId(id);
        if (houseStatuses.isEmpty()) {
            return new ResponseEntity<>("Bạn chưa tạo nhà nào!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(houseStatuses, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/set")
    public ResponseEntity<String> setHouseStatus(@RequestBody HouseStatus houseStatus) {
        User originUser = userDetailsService.getCurrentUser();
        House house = houseService.findById(houseStatus.getHouse().getId());

        boolean isHost = houseService.isHost(originUser, house);
        if (!isHost) {
            return new ResponseEntity<>("Bạn không có quyền xem nhà này!", HttpStatus.BAD_REQUEST);
        }

        Long id = houseStatus.getHouse().getId();
        Date beginDate = houseStatus.getBeginDate();
        Date endDate = houseStatus.getEndDate();

        Status status = statusService.findByStatus(houseStatus.getStatus().getName());
        Status availableStatus = statusService.findByStatus(StatusHouse.AVAILABLE);

        if (beginDate.getTime() > endDate.getTime()) {
            return new ResponseEntity<>("Thời gian checkout phải sau thời gian checkin.", HttpStatus.BAD_REQUEST);
        }

        HouseStatus houseStatus1 = houseStatusService.findHouseStatusAvailable(beginDate, endDate, id);

        if (houseStatus1 == null) {
            return new ResponseEntity<>("Khoảng thời gian này đã có người đặt, vui lòng chọn ngày khác.", HttpStatus.BAD_REQUEST);
        }
        
        Date beginDate1 = houseStatus1.getBeginDate();
        Date endDate1 = houseStatus1.getEndDate();

        if ((beginDate == beginDate1) && (endDate == endDate1)) {
            houseStatus1.setStatus(status);
            houseStatusService.save(houseStatus1);
            return new ResponseEntity<>("Tạo order thành công!", HttpStatus.CREATED);
        }

        boolean isBeginDayEqual = !beginDate.toString().equals(beginDate1.toString());
        if (isBeginDayEqual) {
            HouseStatus houseStatus2 = new HouseStatus(house, beginDate1, new Date(beginDate.getTime() - 86400000L), availableStatus);
            houseStatusService.save(houseStatus2);
        }

        HouseStatus originHouseStatus = new HouseStatus(house, beginDate, endDate, status);
        houseStatusService.save(originHouseStatus);

        boolean isEndDayEqual = !endDate.toString().equals(endDate1.toString());
        if (isEndDayEqual) {
            HouseStatus houseStatus3 = new HouseStatus(house, new Date(endDate.getTime() + 86400000L), endDate1, availableStatus);
            houseStatusService.save(houseStatus3);
        }

        houseStatusService.deleteById(houseStatus1.getId());
        return new ResponseEntity<>("Tạo order thành công!", HttpStatus.CREATED);
    }
}
