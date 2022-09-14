package com.wotos.wotosstatisticsservice.util.feign.wot;

import com.wotos.wotosstatisticsservice.config.FeignConfig;
import com.wotos.wotosstatisticsservice.util.model.wot.WotApiResponse;
import com.wotos.wotosstatisticsservice.util.model.wot.player.WotPlayerDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "WotAccountsFeignClient", url = "${env.urls.world_of_tanks_api}", configuration = FeignConfig.class)
@RequestMapping("/account")
public interface WotAccountsFeignClient {

    @GetMapping(value = "/info/", consumes = "application/json")
    ResponseEntity<WotApiResponse<Map<Integer, WotPlayerDetails>>> getPlayerDetails(
            @RequestParam(value = "application_id") String appId,
            @RequestParam(value = "access_token", required = false) String accessToken,
            @RequestParam(value = "extra", required = false) String extra,
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "account_id") Integer accountId
    );

}
