package com.wotos.wotosstatisticsservice.controller;

import com.wotos.wotosstatisticsservice.dao.StatisticsSnapshot;
import com.wotos.wotosstatisticsservice.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    StatsService statsService;

//    @GetMapping("/{playerID}")
//    public PlayerPersonalData getPlayerPersonalData(@PathVariable("playerID") int playerID) {
//        return statsService.fetchPlayerPersonalData(playerID);
//    }

//    @GetMapping("/{playerID}")
//    public ResponseEntity<PlayerStatistics> getPlayerStatistics(@PathVariable("playerID") int playerID) {
//        return statsService.getAveragePlayerStatistics(playerID);
//    }

//    @GetMapping("/{playerID}/{tankID}")
//    public ResponseEntity<TankStatistics> getTankStatisticsByPlayer(@PathVariable("playerID") int playerID, @PathVariable("tankID") int tankID) {
//        return statsService.getTankStatisticsByPlayerID(playerID, tankID);
//    }

    @GetMapping("/{playerID}/all")
    public ResponseEntity<List<StatisticsSnapshot>> getAlltankStatisticsByPlayerID(@PathVariable("playerID") int playerID) {
        List<StatisticsSnapshot> allTankStatistics = statsService.getAllTankStatisticsByPlayerID(playerID);

        if (allTankStatistics.size() > 0) {
            return new ResponseEntity<>(allTankStatistics, HttpStatus.FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
