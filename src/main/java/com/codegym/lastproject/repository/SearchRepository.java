package com.codegym.lastproject.repository;

import com.codegym.lastproject.model.House;

import java.util.List;

public interface SearchRepository {
    List<House> search(Long bedroomNumber, Long bathroomNumber, Long price, String address);
}
