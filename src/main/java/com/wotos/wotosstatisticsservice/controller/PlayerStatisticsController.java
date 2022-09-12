package com.wotos.wotosstatisticsservice.controller;

import com.wotos.wotosstatisticsservice.dao.PlayerStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.service.PlayerStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class PlayerStatisticsController {

    @Autowired
    PlayerStatisticsService playerStatisticsService;

    @GetMapping("/players")
    public ResponseEntity<Map<Integer, Map<String, List<PlayerStatisticsSnapshot>>>> getPlayerStatisticsSnapshots(
            @RequestParam(value = "accountIds") List<Integer> accountIds,
            @RequestParam(value = "gameModes") List<String> gameModes
    ) {
        return new ResponseEntity<>(playerStatisticsService.getPlayerStatisticsSnapshotsMap(accountIds, gameModes), HttpStatus.FOUND);
    }

    @PostMapping("/players")
    public ResponseEntity<HttpStatus> createPlayerStatisticsSnapshots(
            @RequestParam(value = "accountIds") List<Integer> accountIds
    ) {
        playerStatisticsService.createPlayerStatisticsSnapshotsByAccountIds(accountIds);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/recentWn8")
    public ResponseEntity<Map<Integer, Map<String, Float>>> getPlayerRecentWn8(
            @RequestParam(value = "accountIds") List<Integer> accountIds,
            @RequestParam(value = "gameModes") List<String> gameModes,
            @RequestParam(value = "timestamp") Long timestamp
    ) {
        return new ResponseEntity<>(playerStatisticsService.getPlayerRecentAverageWn8Map(accountIds, gameModes, timestamp), HttpStatus.FOUND);
    }

}