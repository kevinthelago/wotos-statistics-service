package com.wotos.wotosstatisticsservice.repo;

import com.wotos.wotosstatisticsservice.dao.StatisticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StatisticsSnapshotsRepository extends JpaRepository<StatisticsSnapshot, Integer> {

    Optional<List<StatisticsSnapshot>> findAllStatisticsSnapshotsByPlayerId(int playerId);

//    @Query(value = "SELECT total_battles FROM statistics_snapshots s WHERE player_id = ?1 AND tank_id = ?2 ORDER BY total_battles asc")
    Optional<List<StatisticsSnapshot>> findByPlayerIdAndTankId(int playerId, int tankId);

}
