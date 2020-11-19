package com.wotos.wotosstatisticsservice.controller;

import com.wotos.wotosstatisticsservice.model.ExpectedStatistics;
import com.wotos.wotosstatisticsservice.model.PlayerStatistics;
import com.wotos.wotosstatisticsservice.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    StatsService statsService;

    @GetMapping("/{playerID}")
    public Optional<PlayerStatistics> getPlayerStatistics(@PathVariable("playerID") int playerID) {
        return statsService.getPlayerStatistics(playerID);
    }

    /*
    * Everything bellow needs to eventually be private
    * */

    @GetMapping("/expected")
    public List<ExpectedStatistics> fetchExpectedStats() {
        return statsService.fetchExpectedStatistics();
    }

}
