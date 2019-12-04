package com.codegym.lastproject.service.impl;

import com.codegym.lastproject.model.House;
import com.codegym.lastproject.model.HouseStatus;
import com.codegym.lastproject.model.Status;
import com.codegym.lastproject.model.util.StatusHouse;
import com.codegym.lastproject.repository.HouseStatusRepository;
import com.codegym.lastproject.service.HouseService;
import com.codegym.lastproject.service.HouseStatusService;
import com.codegym.lastproject.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class HouseStatusServiceImpl implements HouseStatusService {
    @Autowired
    private HouseStatusRepository houseStatusRepository;

    @Autowired
    private StatusService statusService;

    @Autowired
    private HouseService houseService;

    @Override
    public List<HouseStatus> findAll() {
        return houseStatusRepository.findAll();
    }

    @Override
    public HouseStatus findById(Long id) {
        return houseStatusRepository.findById(id).get();
    }

    @Override
    public void save(HouseStatus houseStatus) {
        houseStatusRepository.save(houseStatus);
    }

    @Override
    public void deleteById(Long id) {
        houseStatusRepository.deleteById(id);
    }

    @Override
    public List<HouseStatus> findAllByHouseId(Long houseId) {
        return houseStatusRepository.findAllByHouseId(houseId);
    }

    @Override
    public HouseStatus findHouseStatusAvailable(Date beginDate, Date endDate, Long houseId) {
        return houseStatusRepository.findByBeginDateLessThanEqualAndEndDateGreaterThanEqualAndHouseIdAndStatus(beginDate, endDate, houseId, statusService.findByStatus(StatusHouse.AVAILABLE));
    }

    @Override
    public HouseStatus findHouseStatusBegin(Date beginDate, Long houseId) {
        return houseStatusRepository.findByBeginDateEqualsAndHouseIdAndStatus(beginDate, houseId, statusService.findByStatus(StatusHouse.AVAILABLE));
    }

    @Override
    public HouseStatus findHouseStatusEnd(Date endDate, Long houseId) {
        return houseStatusRepository.findByEndDateEqualsAndHouseIdAndStatus(endDate, houseId, statusService.findByStatus(StatusHouse.AVAILABLE));
    }

    @Override
    public HouseStatus findHouseStatusBooked(Date beginDate, Date endDate, Long houseId) {
        return houseStatusRepository.findByBeginDateEqualsAndEndDateEqualsAndHouseIdAndStatus(beginDate, endDate, houseId, statusService.findByStatus(StatusHouse.BOOKED));
    }

    @Override
    public List<HouseStatus> findHouseStatusInMonth(Date begin, Date end, Long houseId) {
        return houseStatusRepository.findAllByEndDateGreaterThanEqualAndEndDateLessThanAndHouseIdAndStatus(begin, end, houseId, statusService.findByStatus(StatusHouse.BOOKED));
    }

    @Override
    public void setStatusNewHouse() {
        HouseStatus houseStatus = new HouseStatus();

        Long id = houseService.findMaxHouseId();
        House house = houseService.findById(id);
        System.out.println(house.getId());
        houseStatus.setHouse(house);

        Date beginDate = new Date(System.currentTimeMillis());
        houseStatus.setBeginDate(beginDate);

        Date endDate = new Date(System.currentTimeMillis() + 31536000000L);
        houseStatus.setEndDate(endDate);

        Status status = statusService.findByStatus(StatusHouse.AVAILABLE);
        houseStatus.setStatus(status);

        houseStatusRepository.save(houseStatus);
    }
}
