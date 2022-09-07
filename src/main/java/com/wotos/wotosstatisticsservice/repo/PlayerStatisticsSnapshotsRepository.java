package com.wotos.wotosstatisticsservice.repo;

import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerStatisticsSnapshotsRepository extends JpaRepository<PlayerStatisticsSnapshot, Integer> {

    Optional<List<PlayerStatisticsSnapshot>> findByAccountIdAndGameMode(Integer accountId, String gameMode);

    Optional<Integer> findHighestTotalBattlesByAccountIdAndGameMode(Integer accountId, String gameMode);

}
