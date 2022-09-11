package com.wotos.wotosstatisticsservice.repo;

import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlayerStatisticsSnapshotsRepository extends JpaRepository<PlayerStatisticsSnapshot, Integer> {

    Optional<List<PlayerStatisticsSnapshot>> findByAccountIdAndGameMode(Integer accountId, String gameMode);

    @Query(value = "SELECT MAX(total_battles) FROM player_statistics_snapshots WHERE account_id = ?1 AND game_mode = ?2", nativeQuery = true)
    Optional<Integer> findHighestTotalBattlesByAccountIdAndGameMode(Integer accountId, String gameMode);

}
