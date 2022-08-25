package com.wotos.wotosstatisticsservice.repo;

import com.wotos.wotosstatisticsservice.dao.StatisticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatisticsSnapshotsRepository extends JpaRepository<StatisticsSnapshot, Integer> {

    Optional<List<StatisticsSnapshot>> findAllStatisticsSnapshotsByPlayerId(int playerId);

    Optional<StatisticsSnapshot> findByPlayerIdAndTankId(int playerId, int tankId);

}
