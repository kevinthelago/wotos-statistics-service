package com.wotos.wotosstatisticsservice.repo;

import com.wotos.wotosstatisticsservice.dao.PlayerStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerStatisticsRepository extends JpaRepository<PlayerStatistics, Integer> {
}
