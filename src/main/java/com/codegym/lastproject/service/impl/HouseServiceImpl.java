package com.codegym.lastproject.service.impl;

import com.codegym.lastproject.model.House;
import com.codegym.lastproject.model.OrderHouse;
import com.codegym.lastproject.model.User;
import com.codegym.lastproject.model.util.StatusOrder;
import com.codegym.lastproject.repository.HouseRepository;
import com.codegym.lastproject.repository.SearchRepository;
import com.codegym.lastproject.security.service.UserDetailsServiceImpl;
import com.codegym.lastproject.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class HouseServiceImpl implements HouseService {
    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public List<House> findAll() {
        return houseRepository.findAll();
    }

    @Override
    public List<House> findByHostId(Long hostId) {
        return houseRepository.findByUserId(hostId);
    }

    @Override
    public House findById(Long id) {
        return houseRepository.findById(id).get();
    }

    @Override
    public void save(House house) {
        houseRepository.save(house);
    }

    @Override
    public void deleteHouse(Long id) {
        houseRepository.deleteById(id);
    }

    @Override
    public Long findMaxHouseId() {
        return houseRepository.findMaxHouseId();
    }

    @Override
    public boolean isHost(User user, House house) {
        List<House> houses = houseRepository.findByUserId(user.getId());
        return houses.contains(house);
    }

    @Override
    public boolean isConformity(OrderHouse orderHouse) {
        User originUser = userDetailsService.getCurrentUser();
        StatusOrder statusOrder = orderHouse.getOrderStatus().getName();
        boolean isProcessing = (statusOrder == StatusOrder.PROCESSING);
        House house = houseRepository.findById(orderHouse.getHouse().getId()).get();

        boolean isHost = isHost(originUser, house);
        return orderHouse == null || house == null || !isHost || !isProcessing;
    }

    @Override
    public List<House> search(Long bedroomNumber, Long bathroomNumber, Long price, String address, Date beginDate, Date endDate) {
        return searchRepository.search(bedroomNumber, bathroomNumber, price, address, beginDate, endDate);
    }
}
