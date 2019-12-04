package com.codegym.lastproject.repository;

import com.codegym.lastproject.model.HouseStatus;
import com.codegym.lastproject.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface HouseStatusRepository extends JpaRepository<HouseStatus, Long> {
    List<HouseStatus> findAllByHouseId (Long houseId);

    HouseStatus findByBeginDateLessThanEqualAndEndDateGreaterThanEqualAndHouseIdAndStatus
            (Date beginDate, Date endDate, Long houseId, Status status);

    HouseStatus findByBeginDateEqualsAndHouseIdAndStatus (Date beginDate, Long houseId, Status status);

    HouseStatus findByEndDateEqualsAndHouseIdAndStatus (Date endDate, Long houseId, Status status);

    HouseStatus findByBeginDateEqualsAndEndDateEqualsAndHouseIdAndStatus (Date beginDate, Date endDate, Long houseId, Status status);

    List<HouseStatus> findAllByEndDateGreaterThanEqualAndEndDateLessThanAndHouseIdAndStatus(Date begin, Date end, Long houseId, Status status);
}
