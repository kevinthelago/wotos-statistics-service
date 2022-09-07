package com.wotos.wotosstatisticsservice.repo;

import com.wotos.wotosstatisticsservice.dao.VehicleStatisticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VehicleStatisticsSnapshotsRepository extends JpaRepository<VehicleStatisticsSnapshot, Integer> {

    Optional<List<VehicleStatisticsSnapshot>> findByAccountIdAndVehicleIdIn(Integer accountId, List<Integer> vehicleIds);

    Optional<List<VehicleStatisticsSnapshot>> findByAccountIdAndVehicleIdAndGameMode(Integer accountId, Integer vehicleId, String gameMode);

    @Query(value = "SELECT MAX(total_battles) FROM vehicle_statistics_snapshots WHERE account_id = ?1 AND vehicle_id = ?2 AND game_mode = ?3", nativeQuery = true)
    Optional<Integer> findHighestTotalBattlesByAccountIdAndVehicleId(Integer accountId, Integer vehicleId, String gameMode);

    // ToDo: Validate this query is returning correct values
    @Query(value = "SELECT avg(average_wn8) FROM vehicle_statistics_snapshots where total_battles in (SELECT max(total_battles) FROM vehicle_statistics_snapshots group by vehicle_id) AND account_id = ?1 AND game_mode = ?2", nativeQuery = true)
    Optional<Float> averageAverageWn8ByGameModeAndAccountId(Integer accountId, String gameMode);

}
