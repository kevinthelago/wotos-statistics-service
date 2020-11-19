package com.wotos.wotosstatisticsservice.dao;

import com.wotos.wotosstatisticsservice.model.ExpectedStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpectedStatisticsRepository extends JpaRepository<ExpectedStatistics, Integer> {

//    Optional<List<ExpectedStats>> getAllExpectedStats();
//
//    Optional<ExpectedStats> getExpectedStats(int tankId);
//
//    ExpectedStats saveExpectedStats(ExpectedStats expectedStats);
//
//    ExpectedStats updateExpectedStats(ExpectedStats expectedStats);

}
