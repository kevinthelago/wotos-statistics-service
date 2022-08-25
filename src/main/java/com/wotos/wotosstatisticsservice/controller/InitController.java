package com.wotos.wotosstatisticsservice.controller;

import com.wotos.wotosstatisticsservice.dao.ExpectedStatistics;
import com.wotos.wotosstatisticsservice.dao.StatisticsSnapshot;
import com.wotos.wotosstatisticsservice.service.ExpectedStatisticsService;
import com.wotos.wotosstatisticsservice.service.WotApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/init")
public class InitController {

    @Autowired
    ExpectedStatisticsService expectedStatisticsService;
    @Autowired
    WotApiService wotApiService;

    @GetMapping("/expected")
    public List<ExpectedStatistics> initExpectedStatistics() {
        return expectedStatisticsService.validateOrInitExpectedStatistics();
    }

    @GetMapping("/fuckyou/{playerId}")
    public List<StatisticsSnapshot> getShit(@PathVariable("playerId") int playerId) {
        return wotApiService.getTanks(playerId);
    }

}
