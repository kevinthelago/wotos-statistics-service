package com.wotos.wotosstatisticsservice.client.wot;

import com.wotos.wotosstatisticsservice.config.FeignConfig;
import com.wotos.wotosstatisticsservice.client.wot.player.WotPlayerDetails;
import com.wotos.wotosstatisticsservice.validation.constraints.Language;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "WotAccountsFeignClient", url = "${env.urls.world_of_tanks_api}", configuration = FeignConfig.class)
@RequestMapping("/account")
public interface WotAccountsFeignClient {

    @GetMapping(value = "/info/", consumes = "application/json")
    ResponseEntity<WotApiResponse<Map<Integer, WotPlayerDetails>>> getPlayerDetails(
            @RequestParam(value = "application_id") String appId,
            @RequestParam(value = "access_token", required = false) String accessToken,
            @RequestParam(value = "extra", required = false) String[] extra,
            @RequestParam(value = "fields", required = false) String[] fields,
            @RequestParam(value = "language", required = false, defaultValue = "en") @Language String language,
            @RequestParam(value = "account_id") Integer[] accountIds
    );

}
