package com.wotos.wotosstatisticsservice.controller;

import com.wotos.wotosstatisticsservice.dao.VehicleStatisticsSnapshot;
import com.wotos.wotosstatisticsservice.service.VehicleStatisticsService;
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
public class VehicleStatisticsController {

    private final VehicleStatisticsService vehicleStatisticsService;

    public VehicleStatisticsController(VehicleStatisticsService vehicleStatisticsService) {
        this.vehicleStatisticsService = vehicleStatisticsService;
    }

    /**
     * Returns existing vehicle statistics snapshots grouped by account, vehicle, and game mode.
     *
     * @param accountIds WoT account IDs to fetch snapshots for
     * @param vehicleIds vehicles to filter by (optional)
     * @param gameModes  game modes to include (validated against {@link GameMode}, optional)
     * @return 200 with a map of {accountId -> {vehicleId -> {gameMode -> [snapshots]}}}
     */
    @GetMapping("/stats/vehicles")
    public ResponseEntity<Map<Integer, Map<Integer, Map<String, List<VehicleStatisticsSnapshot>>>>> getPlayerVehicleStatisticsSnapshots(
            @RequestParam(value = "accountIds") Integer[] accountIds,
            @RequestParam(value = "vehicleIds", required = false) Integer[] vehicleIds,
            @RequestParam(value = "gameModes", required = false) @GameMode String[] gameModes
    ) {
        return ResponseEntity.ok(vehicleStatisticsService.getPlayerVehicleStatisticsSnapshotsMap(accountIds, vehicleIds, gameModes));
    }

    /**
     * Creates fresh vehicle statistics snapshots for the given accounts (optionally
     * scoped to specific vehicles) by pulling the latest data from the WoT API.
     *
     * @param accountIds WoT account IDs to snapshot
     * @param vehicleIds vehicles to snapshot (optional; all vehicles if omitted)
     * @return 201 with the newly created snapshots keyed by accountId, vehicleId, and game mode
     */
    @PostMapping("/stats/vehicles")
    public ResponseEntity<Map<Integer, Map<Integer, Map<String, VehicleStatisticsSnapshot>>>> createPlayerVehicleStatisticsSnapshots(
            @RequestParam(value = "accountIds") Integer[] accountIds,
            @RequestParam(value = "vehicleIds", required = false) Integer[] vehicleIds
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(vehicleStatisticsService.createPlayerVehicleStatisticsSnapshots(accountIds, vehicleIds));
    }

}
