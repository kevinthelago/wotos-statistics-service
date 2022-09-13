package com.wotos.wotosstatisticsservice.controller;

import com.wotos.wotosstatisticsservice.dao.VehicleStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.service.VehicleStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            @RequestParam(value = "accountIds") Integer[] accountIds,
            @RequestParam(value = "vehicleIds") Integer[] vehicleIds,
            @RequestParam(value = "gameModes") String[] gameModes
    ) {
        return new ResponseEntity<>(vehicleStatisticsService.getPlayerVehicleStatisticsSnapshotsMap(accountIds, vehicleIds, gameModes), HttpStatus.OK);
    }

    @PostMapping("/vehicles")
    public ResponseEntity<HttpStatus> createPlayerVehicleStatisticsSnapshots(
            @RequestParam(value = "accountIds") Integer[] accountIds,
            @RequestParam(value = "vehicleIds", required = false) Integer[] vehicleIds
    ) {
        vehicleStatisticsService.createPlayerVehicleStatisticsSnapshots(accountIds, vehicleIds);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
