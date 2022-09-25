package com.wotos.wotosstatisticsservice.repo;

import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;

public interface PlayerStatisticsSnapshotsRepository extends JpaRepository<PlayerStatisticsSnapshot, Integer> {

    List<PlayerStatisticsSnapshot> findAllByAccountIdInAndGameModeIn(Integer[] accountId, String[] gameMode);

    default Map<Integer, Map<String, List<PlayerStatisticsSnapshot>>> getPlayerStatisticsMap(Integer[] accountIds, String[] gameMode) {
        return findAllByAccountIdInAndGameModeIn(accountIds, gameMode).stream()
                .collect(
                        groupingBy(PlayerStatisticsSnapshot::getAccountId,
                                groupingBy(PlayerStatisticsSnapshot::getGameMode))
                );
    }

    @Query(value = "SELECT MAX(total_battles) FROM player_statistics_snapshots WHERE account_id = :accountId AND game_mode = :gameMode", nativeQuery = true)
    Optional<Integer> findHighestTotalBattlesByAccountIdAndGameMode(
            @Param("accountId") Integer accountId,
            @Param("gameMode") String gameMode
    );

}
