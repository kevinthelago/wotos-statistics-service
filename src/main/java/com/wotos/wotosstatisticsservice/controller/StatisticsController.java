package com.wotos.wotosstatisticsservice.controller;

import com.wotos.wotosstatisticsservice.dao.StatisticsSnapshot;
import com.wotos.wotosstatisticsservice.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatisticsController {

    @Autowired
    StatisticsService statsService;

    @GetMapping("/tank")
    public ResponseEntity<List<StatisticsSnapshot>> getTankStatisticsByPlayer(@PathParam("playerId") Integer playerId, @PathParam("tankId") Integer tankId) {
        List<StatisticsSnapshot> statisticsSnapshots = statsService.getPlayerTankStatistics(playerId, tankId);

        return new ResponseEntity<>(statisticsSnapshots, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<StatisticsSnapshot>> getAllTankStatisticsByPlayer(@PathParam("playerId") Integer playerId) {
        List<StatisticsSnapshot> allTankStatistics = statsService.getAllPlayerTankStatistics(playerId);

        if (allTankStatistics.size() > 0) {
            return new ResponseEntity<>(allTankStatistics, HttpStatus.FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
