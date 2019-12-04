package com.codegym.lastproject.service;

import com.codegym.lastproject.model.HouseStatus;

import java.sql.Date;
import java.util.List;

public interface HouseStatusService {
    List<HouseStatus> findAll();

    HouseStatus findById(Long id);

    void save(HouseStatus houseStatus);

    void deleteById(Long id);

    List<HouseStatus> findAllByHouseId(Long houseId);

    HouseStatus findHouseStatusAvailable(Date beginDate, Date endDate, Long houseId);

    HouseStatus findHouseStatusBegin(Date beginDate, Long houseId);

    HouseStatus findHouseStatusEnd(Date endDate, Long houseId);

    HouseStatus findHouseStatusBooked(Date beginDate, Date endDate, Long houseId);

    void setStatusNewHouse();

    List<HouseStatus> findHouseStatusInMonth(Date begin, Date end, Long houseId);
}
