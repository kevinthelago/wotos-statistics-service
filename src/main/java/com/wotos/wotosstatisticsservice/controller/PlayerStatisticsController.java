package com.wotos.wotosstatisticsservice.controller;

import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.service.PlayerStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
public class PlayerStatisticsController {

    @Autowired
    PlayerStatisticsService playerStatisticsService;

    @GetMapping("/stats/players")
    public ResponseEntity<Map<Integer, Map<String, List<PlayerStatisticsSnapshot>>>> getPlayerStatisticsSnapshots(
            @RequestParam(value = "accountIds") Integer[] accountIds,
            @RequestParam(value = "gameModes") String[] gameModes
    ) {
        return new ResponseEntity<>(playerStatisticsService.getPlayerStatisticsSnapshotsMap(accountIds, gameModes), HttpStatus.FOUND);
    }

    @PostMapping("/stats/players")
    public ResponseEntity<HttpStatus> createPlayerStatisticsSnapshots(
            @RequestParam(value = "accountIds") Integer[] accountIds
    ) {
        playerStatisticsService.createPlayerStatisticsSnapshotsByAccountIds(accountIds);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/stats/recentWn8")
    public ResponseEntity<Map<Integer, Map<String, Float>>> getPlayerRecentWn8(
            @RequestParam(value = "accountIds") Integer[] accountIds,
            @RequestParam(value = "gameModes") String[] gameModes,
            @RequestParam(value = "timestamp") Long timestamp
    ) {
        return new ResponseEntity<>(playerStatisticsService.getPlayerRecentAverageWn8Map(accountIds, gameModes, timestamp), HttpStatus.FOUND);
    }

}