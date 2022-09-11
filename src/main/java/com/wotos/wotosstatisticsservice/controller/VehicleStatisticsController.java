package com.wotos.wotosstatisticsservice.controller;

import com.wotos.wotosstatisticsservice.dao.VehicleStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.service.VehicleStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class VehicleStatisticsController {

    @Autowired
    VehicleStatisticsService vehicleStatisticsService;

    @GetMapping("/vehicles")
    public ResponseEntity<Map<Integer, Map<Integer, Map<String, List<VehicleStatisticsSnapshot>>>>> getPlayerVehicleStatisticsSnapshots(
            @RequestParam("accountIds") List<Integer> accountIds,
            @RequestParam("vehicleIds") List<Integer> vehicleIds
    ) {
        return vehicleStatisticsService.getPlayerVehicleStatisticsSnapshots(accountIds, vehicleIds);
    }

}
