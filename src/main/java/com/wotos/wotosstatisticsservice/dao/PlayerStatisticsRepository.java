package com.wotos.wotosstatisticsservice.dao;

import com.wotos.wotosstatisticsservice.model.PlayerStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerStatisticsRepository extends JpaRepository<PlayerStatistics, Integer> {



}
