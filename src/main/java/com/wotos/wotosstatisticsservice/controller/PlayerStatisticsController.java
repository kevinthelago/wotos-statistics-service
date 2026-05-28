package com.wotos.wotosstatisticsservice.controller;

import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.service.PlayerStatisticsService;
import com.wotos.wotosstatisticsservice.validation.constraints.GameMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
public class PlayerStatisticsController {

    private final PlayerStatisticsService playerStatisticsService;

    public PlayerStatisticsController(PlayerStatisticsService playerStatisticsService) {
        this.playerStatisticsService = playerStatisticsService;
    }

    /**
     * Returns existing player statistics snapshots grouped by account and game mode.
     *
     * @param accountIds WoT account IDs to fetch snapshots for
     * @param gameModes  game modes to include (validated against {@link GameMode})
     * @return 200 with a map of {accountId -> {gameMode -> [snapshots]}}
     */
    @GetMapping("/stats/players")
    public ResponseEntity<Map<Integer, Map<String, List<PlayerStatisticsSnapshot>>>> getPlayerStatisticsSnapshots(
            @RequestParam(value = "accountIds") Integer[] accountIds,
            @RequestParam(value = "gameModes") @GameMode String[] gameModes
    ) {
        return ResponseEntity.ok(playerStatisticsService.getPlayerStatisticsSnapshotsMap(accountIds, gameModes));
    }

    /**
     * Creates fresh player statistics snapshots for the given accounts by pulling
     * the latest data from the WoT API and computing WN8.
     *
     * @param accountIds WoT account IDs to snapshot
     * @return 201 with the newly created snapshots keyed by accountId and game mode
     */
    @PostMapping("/stats/players")
    public ResponseEntity<Map<Integer, Map<String, PlayerStatisticsSnapshot>>> createPlayerStatisticsSnapshots(
            @RequestParam(value = "accountIds") Integer[] accountIds
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(playerStatisticsService.createPlayerStatisticsSnapshotsByAccountIds(accountIds));
    }

}
