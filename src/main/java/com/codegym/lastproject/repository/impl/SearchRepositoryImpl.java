package com.codegym.lastproject.repository.impl;

import com.codegym.lastproject.model.House;
import com.codegym.lastproject.repository.SearchRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.sql.Date;
import java.util.List;

@Repository
public class SearchRepositoryImpl implements SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<House> search(Long bedroomNumber, Long bathroomNumber, Long price, String address, Date beginDate, Date endDate) {
        TypedQuery<House> query = null;
        StringBuilder hsql = new StringBuilder();
        hsql.append("SELECT DISTINCT h FROM House h JOIN HouseStatus s ON h.id = s.house.id WHERE");
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
            hsql.append(" h.address like :address AND");
        }
        if (beginDate != null) {
            hsql.append(" s.beginDate <= :beginDate AND");
        }
        if (endDate != null) {
            hsql.append(" s.endDate >= :endDate AND");
        }
        hsql.append(" s.status.name = 'AVAILABLE'");

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
        if (beginDate != null) {
            query.setParameter("beginDate", beginDate);
        }
        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        return query.getResultList();
    }
}
