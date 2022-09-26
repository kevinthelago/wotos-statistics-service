package com.wotos.wotosstatisticsservice.repo;

import com.wotos.wotosstatisticsservice.dao.VehicleStatisticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

public interface VehicleStatisticsSnapshotsRepository extends JpaRepository<VehicleStatisticsSnapshot, Integer> {

    List<VehicleStatisticsSnapshot> findAllByAccountIdInAndVehicleIdInAndGameModeIn(
            @Param("accountIds") Integer[] accountId,
            @Param("vehicleIds") Integer[] vehicleIds,
            @Param("gameModes") String[] gameModes
    );

    default Map<Integer, Map<Integer, Map<String, List<VehicleStatisticsSnapshot>>>> findAllPlayerVehicleStatisticsMapByAccountId(Integer[] accountIds, Integer[] vehicleIds, String[] gameModes) {
           return findAllByAccountIdInAndVehicleIdInAndGameModeIn(accountIds, vehicleIds, gameModes).stream()
                   .collect(
                           groupingBy(VehicleStatisticsSnapshot::getAccountId,
                                   groupingBy(VehicleStatisticsSnapshot::getVehicleId,
                                           groupingBy(VehicleStatisticsSnapshot::getGameMode)))
                   );
    }

    @Query(value = "SELECT MAX(total_battles) FROM vehicle_statistics_snapshots WHERE account_id = :accountId AND vehicle_id = :vehicleId AND game_mode = :gameMode", nativeQuery = true)
    Optional<Integer> findHighestTotalBattlesByAccountIdAndVehicleId(
            @Param("accountId") Integer accountId,
            @Param("vehicleId") Integer vehicleId,
            @Param("gameMode") String gameMode
    );

    @Query(value = "SELECT avg(average_wn8) FROM vehicle_statistics_snapshots where total_battles in (SELECT max(total_battles) FROM vehicle_statistics_snapshots group by vehicle_id) AND account_id = :accountId AND game_mode = :gameMode", nativeQuery = true)
    Optional<Float> averageAverageWn8ByGameModeAndAccountId(
            @Param("accountId") Integer accountId,
            @Param("gameMode") String gameMode
    );

    @Query(value = "SELECT avg(average_wn8) FROM vehicle_statistics_snapshots where total_battles in (SELECT max(total_battles) FROM vehicle_statistics_snapshots group by vehicle_id) AND account_id = :accountId AND game_mode = :gameMode AND create_timestamp > :timestamp", nativeQuery = true)
    Optional<Float> averageRecentAverageWn8ByGameModeAndAccountId(
            @Param("accountId") Integer accountId,
            @Param("gameMode") String gameMode,
            @Param("timestamp") Long timestamp
    );

}
