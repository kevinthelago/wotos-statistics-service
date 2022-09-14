package com.wotos.wotosstatisticsservice.util.feign.wot;


import com.wotos.wotosstatisticsservice.config.FeignConfig;
import com.wotos.wotosstatisticsservice.util.model.wot.WotApiResponse;
import com.wotos.wotosstatisticsservice.util.model.wot.statistics.WotVehicleStatistics;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;

@FeignClient(name = "WoTPlayerVehiclesFeignClient", url = "${env.urls.world_of_tanks_api}", configuration = FeignConfig.class)
@RequestMapping("/tanks")
public interface WotPlayerVehiclesFeignClient {

    @GetMapping("/stats/")
    ResponseEntity<WotApiResponse<Map<Integer, List<WotVehicleStatistics>>>> getPlayerVehicleStatistics(
            @RequestParam(value = "application_id") String appId,
            @RequestParam(value = "account_id") Integer[] accountIds,
            @RequestParam(value = "access_token", required = false) String accessToken,
            @RequestParam(value = "extra", required = false) String[] extra,
            @RequestParam(value = "fields", required = false) String[] fields,
            @RequestParam(value = "in_garage", required = false) @Min(0) @Max(1) Integer inGarage,
            @RequestParam(value = "language", required = false, defaultValue = "en") String language,
            @RequestParam(value = "tank_id") Integer[] vehicleIds
    );

    @GetMapping("/achievements/")
    ResponseEntity<WotApiResponse<String>> getPlayerVehicleAchievements(
            @RequestParam(value = "application_id") String appId,
            @RequestParam(value = "account_id") Integer[] accountIds,
            @RequestParam(value = "access_token") String accessToken,
            @RequestParam(value = "fields", required = false) String[] fields,
            @RequestParam(value = "in_garage", required = false) @Min(0) @Max(1) Integer inGarage,
            @RequestParam(value = "language", required = false, defaultValue = "en") String language,
            @RequestParam(value = "tank_id") Integer[] vehicleIds
    );

}
