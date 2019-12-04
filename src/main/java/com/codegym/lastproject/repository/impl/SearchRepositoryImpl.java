package com.codegym.lastproject.repository.impl;

import com.codegym.lastproject.model.House;
import com.codegym.lastproject.repository.SearchRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class SearchRepositoryImpl implements SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<House> search(Long bedroomNumber, Long bathroomNumber, Long price, String address) {
        TypedQuery<House> query = null;
        StringBuilder hsql = new StringBuilder();
        hsql.append("SELECT h FROM House h WHERE");
        if (bedroomNumber != null) {
            hsql.append(" h.bedroomNumber = :bedroomNumber AND");
        }
        if (bathroomNumber != null) {
            hsql.append(" h.bathroomNumber = :bathroomNumber AND");
        }
        if (price != null) {
            hsql.append(" h.price = :price AND");
        }
        if (address != null) {
            hsql.append(" h.address like :address");
        }
        query = entityManager.createQuery(hsql.toString(), House.class);
        System.out.println(hsql.toString());
        if (bedroomNumber != null) {
            query.setParameter("bedroomNumber", bedroomNumber);
        }
        if (bathroomNumber != null) {
            query.setParameter("bathroomNumber", bathroomNumber);
        }
        if (price != null) {
            query.setParameter("price", price);
        }
        if (address != null) {
            query.setParameter("address", address);
        }

        return query.getResultList();
    }
}
